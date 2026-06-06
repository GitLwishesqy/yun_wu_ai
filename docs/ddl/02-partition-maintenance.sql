-- ============================================
-- 云悟英语 — 分区自动维护脚本
-- 建议通过 pg_cron 每月1号凌晨执行
-- ============================================

-- 创建 pg_cron 扩展 (需要超级用户权限)
-- CREATE EXTENSION IF NOT EXISTS pg_cron;

-- 函数: 自动创建下月分区
CREATE OR REPLACE FUNCTION auto_create_monthly_partitions()
RETURNS void AS $$
DECLARE
    next_month_start DATE;
    month_after_start DATE;
    partition_name TEXT;
    table_name TEXT;
BEGIN
    -- 计算下个月的第一天
    next_month_start := date_trunc('month', NOW()) + INTERVAL '1 month';
    month_after_start := next_month_start + INTERVAL '1 month';

    -- 为每个分区表创建新分区
    FOREACH table_name IN ARRAY ARRAY['coach_messages', 'audit_logs', 'ai_usage_logs']
    LOOP
        partition_name := table_name || '_y' ||
            to_char(next_month_start, 'YYYY') || 'm' ||
            to_char(next_month_start, 'MM');

        -- 检查分区是否已存在
        IF NOT EXISTS (
            SELECT 1 FROM pg_class WHERE relname = partition_name
        ) THEN
            EXECUTE format(
                'CREATE TABLE %I PARTITION OF %I
                 FOR VALUES FROM (%L) TO (%L)',
                partition_name, table_name, next_month_start, month_after_start
            );
            RAISE NOTICE 'Created partition: %', partition_name;
        ELSE
            RAISE NOTICE 'Partition % already exists', partition_name;
        END IF;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- 函数: 删除过期分区 (保留 N 个月)
CREATE OR REPLACE FUNCTION drop_expired_partitions(retain_months INT DEFAULT 12)
RETURNS void AS $$
DECLARE
    cutoff_date DATE;
    rec RECORD;
BEGIN
    cutoff_date := date_trunc('month', NOW()) - (retain_months || ' months')::INTERVAL;

    FOR rec IN
        SELECT
            n.nspname AS schema_name,
            c.relname AS partition_name,
            pg_get_expr(c.relpartbound, c.oid) AS partition_bound
        FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE c.relispartition
          AND c.relname ~ '^coach_messages_y|^audit_logs_y|^ai_usage_logs_y'
    LOOP
        -- 解析分区上界日期并与 cutoff 比较
        -- 简单处理: 如果分区名中的日期早于 cutoff，则删除
        IF rec.partition_name ~ 'y(\d{4})m(\d{2})' THEN
            DECLARE
                partition_year INT := substring(rec.partition_name FROM 'y(\d{4})m')::INT;
                partition_month INT := substring(rec.partition_name FROM 'm(\d{2})$')::INT;
                partition_date DATE := make_date(partition_year, partition_month, 1);
            BEGIN
                IF partition_date < cutoff_date THEN
                    EXECUTE format('DROP TABLE IF EXISTS %I', rec.partition_name);
                    RAISE NOTICE 'Dropped expired partition: %', rec.partition_name;
                END IF;
            END;
        END IF;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- 函数: 定期维护活跃分区
CREATE OR REPLACE FUNCTION vacuum_active_partitions()
RETURNS void AS $$
DECLARE
    rec RECORD;
BEGIN
    FOR rec IN
        SELECT c.relname AS partition_name
        FROM pg_class c
        WHERE c.relispartition
          AND c.relname ~ '^coach_messages_y|^audit_logs_y|^ai_usage_logs_y'
          AND c.relname >= (
              'coach_messages_y' || to_char(date_trunc('month', NOW()) - INTERVAL '2 months', 'YYYY') || 'm' || to_char(date_trunc('month', NOW()) - INTERVAL '2 months', 'MM')
          )
    LOOP
        RAISE NOTICE 'Vacuuming partition: %', rec.partition_name;
        EXECUTE format('VACUUM ANALYZE %I', rec.partition_name);
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- 注册 pg_cron 定时任务 (如已安装 pg_cron)
-- SELECT cron.schedule(
--     'create-partitions',
--     '0 0 1 * *',     -- 每月1号凌晨
--     'SELECT auto_create_monthly_partitions();'
-- );
--
-- SELECT cron.schedule(
--     'drop-expired-partitions',
--     '0 2 1 * *',     -- 每月1号凌晨2点
--     'SELECT drop_expired_partitions(12);'
-- );
--
-- SELECT cron.schedule(
--     'vacuum-partitions',
--     '0 3 * * 0',     -- 每周日凌晨3点
--     'SELECT vacuum_active_partitions();'
-- );
