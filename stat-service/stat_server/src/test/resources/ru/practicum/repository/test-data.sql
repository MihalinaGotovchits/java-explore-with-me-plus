INSERT INTO stats (id, app, ip, uri, time_stamp)
VALUES (1, 'test-service', '192.168.0.1', '/api/test', '2024-11-10 10:00:00'),
       (2, 'test-service', '192.168.0.2', '/api/test', '2024-11-10 10:10:00'),
       (3, 'test-service', '192.168.0.1', '/api/test', '2024-11-10 10:20:00'),
       (4, 'other-service', '192.168.0.3', '/api/other', '2024-11-15 12:00:00'),
       (5, 'other-service', '192.168.0.3', '/api/other', '2024-11-20 15:00:00'),
       (6, 'test-service', '192.168.0.1', '/api/test', '2024-11-10 10:30:00');