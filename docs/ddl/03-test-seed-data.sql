-- ============================================
-- 云悟英语 — 测试种子数据
-- 用于开发和测试环境
-- ============================================

-- 测试用户 (密码均为 Test123456! → bcrypt hash)
INSERT INTO users (phone, nickname, role, grade_level, grade_detail, cefr_level, status, real_name_verified) VALUES
('13800000001', '小明', 'STUDENT', 'ELEMENTARY', 'GRADE_3', 'A1', 'ACTIVE', FALSE),
('13800000002', '小红', 'STUDENT', 'JUNIOR', 'GRADE_8', 'A2', 'ACTIVE', FALSE),
('13800000003', '小刚', 'STUDENT', 'SENIOR', 'GRADE_11', 'B1', 'ACTIVE', FALSE),
('13800000004', '李先生', 'STUDENT', 'ADULT', 'WORKING', 'B1', 'ACTIVE', TRUE),
('13800000005', '王妈妈', 'PARENT', NULL, NULL, NULL, 'ACTIVE', TRUE),
('13800000006', '张老师', 'TEACHER', NULL, NULL, NULL, 'ACTIVE', TRUE),
('13800000007', '超级管理员', 'SUPER_ADMIN', NULL, NULL, NULL, 'ACTIVE', TRUE);

-- 学习档案
INSERT INTO learner_profiles (user_id, estimated_vocabulary_size, cefr_level, china_standard_level, weaknesses, total_learning_days, total_session_count, total_learning_minutes, streak_days, preferred_topics, learning_goal) VALUES
(1, 200, 'A1', '2', '{"pronunciation":0.6,"grammar":0.4,"vocabulary":0.5}', 30, 20, 300, 5, '["animals","food","sports"]', '能够用英语进行简单的自我介绍和日常对话'),
(2, 800, 'A2', '5', '{"grammar":0.5,"pronunciation":0.3,"fluency":0.4}', 60, 45, 800, 12, '["music","movies","travel"]', '中考英语口语满分'),
(3, 2000, 'B1', '7', '{"vocabulary":0.3,"logic":0.4,"fluency":0.3}', 100, 80, 1500, 30, '["technology","science","education"]', '高考英语听说满分'),
(4, 3500, 'B1', NULL, '{"fluency":0.5,"pronunciation":0.3,"vocabulary":0.2}', 20, 12, 240, 3, '["business","travel","culture"]', '能够流利进行商务英语交流');

-- 场景模板
INSERT INTO scene_templates (name, name_en, description, category, grade_level, difficulty, cefr_level, roles, keywords, target_sentences, is_published, created_by) VALUES
(
    '餐厅点餐',
    'Ordering at a Restaurant',
    '在餐厅用英语点餐的对话练习',
    'DAILY_LIFE',
    'ELEMENTARY',
    1,
    'A1',
    '[{"name":"服务员","name_en":"Waiter","description":"餐厅服务员，负责接待顾客和记录订单"}]',
    '[{"word":"hamburger","translation":"汉堡包"},{"word":"fries","translation":"薯条"},{"word":"cola","translation":"可乐"},{"word":"menu","translation":"菜单"}]',
    '[{"sentence":"I would like to order...","explanation":"用于点餐"},{"sentence":"Can I have the menu, please?","explanation":"请求菜单"}]',
    TRUE, 6
),
(
    '机场值机',
    'Airport Check-in',
    '在机场办理登机手续的英语对话',
    'TRAVEL',
    'JUNIOR',
    2,
    'A2',
    '[{"name":"地勤人员","name_en":"Check-in Agent","description":"航空公司地勤，办理登机手续"}]',
    '[{"word":"passport","translation":"护照"},{"word":"boarding pass","translation":"登机牌"},{"word":"luggage","translation":"行李"},{"word":"aisle seat","translation":"靠过道座位"}]',
    '[{"sentence":"I would like to check in for flight...","explanation":"办理登机"},{"sentence":"How many pieces of luggage do you have?","explanation":"询问行李数量"}]',
    TRUE, 6
),
(
    '雅思口语 Part 2',
    'IELTS Speaking Part 2',
    '雅思口语第二部分独立陈述练习',
    'EXAM',
    'ADULT',
    4,
    'B2',
    '[{"name":"考官","name_en":"Examiner","description":"雅思口语考官"}]',
    '[{"word":"describe","translation":"描述"},{"word":"experience","translation":"经历"},{"word":"memorable","translation":"难忘的"}]',
    '[{"sentence":"I''m going to give you a topic and I''d like you to talk about it for 1 to 2 minutes.","explanation":"雅思口语Part2开篇语"}]',
    TRUE, 6
),
(
    '商务会议',
    'Business Meeting',
    '模拟商务英语会议场景',
    'BUSINESS',
    'ADULT',
    3,
    'B1',
    '[{"name":"会议主持人","name_en":"Meeting Chair","description":"主持商务会议"}]',
    '[{"word":"agenda","translation":"议程"},{"word":"deadline","translation":"截止日期"},{"word":"proposal","translation":"提案"},{"word":"budget","translation":"预算"}]',
    '[{"sentence":"Let''s go over the agenda for today''s meeting.","explanation":"开始会议"},{"sentence":"I''d like to propose that we...","explanation":"提出建议"}]',
    TRUE, 6
),
(
    '自我介绍',
    'Self Introduction',
    '练习用英语进行自我介绍',
    'DAILY_LIFE',
    'ELEMENTARY',
    1,
    'A1',
    '[{"name":"新朋友","name_en":"New Friend","description":"刚认识的朋友"}]',
    '[{"word":"name","translation":"名字"},{"word":"hobby","translation":"爱好"},{"word":"favorite","translation":"最喜欢的"}]',
    '[{"sentence":"Hi, my name is...","explanation":"自我介绍开头"},{"sentence":"Nice to meet you!","explanation":"问好"}]',
    TRUE, 6
),
(
    '看医生',
    'Visiting the Doctor',
    '在诊所描述病症的英语对话',
    'DAILY_LIFE',
    'JUNIOR',
    2,
    'A2',
    '[{"name":"医生","name_en":"Doctor","description":"诊所医生"}]',
    '[{"word":"headache","translation":"头疼"},{"word":"fever","translation":"发烧"},{"word":"prescription","translation":"处方"},{"word":"symptom","translation":"症状"}]',
    '[{"sentence":"What seems to be the problem?","explanation":"医生询问病情"},{"sentence":"I have been feeling...","explanation":"描述症状"}]',
    TRUE, 6
);

-- 词汇库
INSERT INTO vocabulary_library (word, word_lower, pronunciation, translation, part_of_speech, cefr_level, china_grade, difficulty, example_sentences, exam_tags) VALUES
('hamburger', 'hamburger', '/ˈhæmbɜːrɡər/', '汉堡包', 'noun', 'A1', 'GRADE_3', 1, '[{"en":"I would like a hamburger, please.","zh":"我想要一个汉堡包。"}]', '[]'),
('restaurant', 'restaurant', '/ˈrestrɒnt/', '餐厅', 'noun', 'A1', 'GRADE_4', 1, '[{"en":"Let''s go to a restaurant.","zh":"我们去餐厅吧。"}]', '["KET"]'),
('passport', 'passport', '/ˈpæspɔːrt/', '护照', 'noun', 'A2', 'GRADE_7', 2, '[{"en":"Please show me your passport.","zh":"请出示你的护照。"}]', '["KET","PET"]'),
('experience', 'experience', '/ɪkˈspɪriəns/', '经历；经验', 'noun', 'B1', 'GRADE_9', 3, '[{"en":"It was an unforgettable experience.","zh":"那是一次难忘的经历。"}]', '["PET","IELTS"]'),
('delicious', 'delicious', '/dɪˈlɪʃəs/', '美味的', 'adjective', 'A1', 'GRADE_5', 1, '[{"en":"The food is delicious!","zh":"这食物很美味！"}]', '["KET"]'),
('schedule', 'schedule', '/ˈʃedjuːl/', '日程安排', 'noun', 'B1', 'GRADE_10', 3, '[{"en":"Let me check my schedule.","zh":"让我看看我的日程。"}]', '["PET","CET4"]'),
('environment', 'environment', '/ɪnˈvaɪrənmənt/', '环境', 'noun', 'B1', 'GRADE_9', 3, '[{"en":"We should protect the environment.","zh":"我们应该保护环境。"}]', '["PET","IELTS"]'),
('negotiate', 'negotiate', '/nɪˈɡoʊʃieɪt/', '谈判', 'verb', 'B2', 'GRADE_12', 4, '[{"en":"We need to negotiate the terms.","zh":"我们需要谈判条款。"}]', '["IELTS","TOEFL","CET6"]');

-- 家长绑定
INSERT INTO parent_student_bindings (parent_id, student_id, binding_status, relationship, approved_at) VALUES
(5, 1, 'ACTIVE', 'MOTHER', NOW()),
(5, 2, 'ACTIVE', 'MOTHER', NOW());
