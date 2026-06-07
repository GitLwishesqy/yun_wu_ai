-- 将所有 TIMESTAMPTZ 列转换为 TIMESTAMP (解决 Java LocalDateTime 兼容问题)
DO $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN
        SELECT table_name, column_name
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND data_type = 'timestamp with time zone'
    LOOP
        EXECUTE format('ALTER TABLE %I ALTER COLUMN %I TYPE TIMESTAMP', r.table_name, r.column_name);
    END LOOP;
END $$;
