-- 상위 카테고리 (뮤지컬, 연극, 콘서트)
INSERT INTO categories (category_id, parent_id, name) VALUES (1, NULL, '뮤지컬');
INSERT INTO categories (category_id, parent_id, name) VALUES (2, NULL, '연극');
INSERT INTO categories (category_id, parent_id, name) VALUES (3, NULL, '콘서트');

-- 뮤지컬 하위 카테고리
INSERT INTO categories (category_id, parent_id, name) VALUES (4, 1, '멜로/로맨스');
INSERT INTO categories (category_id, parent_id, name) VALUES (5, 1, '스릴러/공포');
INSERT INTO categories (category_id, parent_id, name) VALUES (6, 1, '코미디');

-- 연극 하위 카테고리
INSERT INTO categories (category_id, parent_id, name) VALUES (7, 2, '멜로/로맨스');
INSERT INTO categories (category_id, parent_id, name) VALUES (8, 2, '스릴러/공포');
INSERT INTO categories (category_id, parent_id, name) VALUES (9, 2, '코미디');

-- 콘서트 하위 카테고리
INSERT INTO categories (category_id, parent_id, name) VALUES (10, 3, '발라드');
INSERT INTO categories (category_id, parent_id, name) VALUES (11, 3, '내한');
INSERT INTO categories (category_id, parent_id, name) VALUES (12, 3, '랩/힙합');
INSERT INTO categories (category_id, parent_id, name) VALUES (13, 3, '페스티벌');
INSERT INTO categories (category_id, parent_id, name) VALUES (14, 3, '인디');
INSERT INTO categories (category_id, parent_id, name) VALUES (15, 3, '아이돌');
