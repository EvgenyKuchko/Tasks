INSERT INTO users
VALUES
(1, 'ADM', '$2a$10$CQP7N0T7nh89.PpoeqPh4OpAiKdeeEygci0W/tP.sOfaAh91izbBW', 'admin'),
(2, 'Samanta', '$2a$10$38wqik5.EAgHQ0YbO.QcWOq0i0R1BXRHgvKO/WBvX30Pi9.JONS9W', 'sam332'),
(3, 'Tom', '$2a$10$kZ8JuBgWLbgYCxsA9G7SBerhOLCVG6vkUcbeXEQhGwJQhed0yJ7oC', 'tomek');

INSERT INTO user_role
VALUES
(1, 'USER'),
(1, 'ADMIN'),
(2, 'USER'),
(3, 'USER');

INSERT INTO tasks
VALUES
(1, CURRENT_DATE, 'tennis', 'training at 17:00', 'ACTIVE', 2),
(2, CURRENT_DATE, 'workshop', 'data structure workshop', 'ACTIVE', 2),
(3, CURRENT_DATE, 'shopping', 'clothes shopping', 'DONE', 2),
(4, CURRENT_DATE - INTERVAL 2 DAY, 'football', 'go to a football match', 'CANCELED', 2),
(5, CURRENT_DATE - INTERVAL 2 DAY, 'date', 'date in restaurant at 20:00', 'DONE', 2),
(6, CURRENT_DATE - INTERVAL 2 DAY, 'spa', 'spa procedures', 'DONE', 2),
(7, CURRENT_DATE + INTERVAL 3 DAY, 'swimming', 'training at 10:00', 'ACTIVE', 2),
(8, CURRENT_DATE + INTERVAL 3 DAY, 'reading', 'read the book "Morphia"', 'ACTIVE', 2),
(9, CURRENT_DATE + INTERVAL 4 DAY, 'friends', 'catch up with friends', 'ACTIVE', 2),
(10, CURRENT_DATE, 'cinema', 'go to a cinema', 'ACTIVE', 3),
(11, CURRENT_DATE + INTERVAL 2 DAY, 'english', 'english lesson', 'ACTIVE', 3),
(12, CURRENT_DATE + INTERVAL 2 DAY, 'shooting range', 'shooting practice', 'CANCELED', 3),
(13, CURRENT_DATE + INTERVAL 3 DAY, 'relatives', 'visit distant relatives', 'ACTIVE', 3),
(14, CURRENT_DATE + INTERVAL 6 DAY, 'concert', 'go to a concert', 'ACTIVE', 3),
(15, CURRENT_DATE + INTERVAL 6 DAY, 'gym', 'gym training', 'ACTIVE', 3);