-- 插入初始场景数据 (created_by=NULL 避免外键约束)
INSERT INTO scene_templates (name, name_en, description, category, grade_level, difficulty, cefr_level, roles, keywords, target_sentences, is_published, created_by) VALUES
('餐厅点餐', 'Ordering', '在餐厅用英语点餐', 'DAILY_LIFE', 'ELEMENTARY', 1, 'A1', '[{"name":"服务员","name_en":"Waiter"}]', '[{"word":"hamburger","translation":"汉堡包"}]', '[{"sentence":"I would like to order..."}]', true, NULL),
('机场值机', 'Airport', '在机场办理登机手续', 'TRAVEL', 'JUNIOR', 2, 'A2', '[{"name":"地勤","name_en":"Agent"}]', '[{"word":"passport","translation":"护照"}]', '[{"sentence":"Check in please"}]', true, NULL),
('自我介绍', 'Self Intro', '用英语自我介绍', 'DAILY_LIFE', 'ELEMENTARY', 1, 'A1', '[{"name":"朋友","name_en":"Friend"}]', '[{"word":"name","translation":"名字"}]', '[{"sentence":"Hi, my name is..."}]', true, NULL);
