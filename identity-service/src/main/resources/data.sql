INSERT INTO role (id, code, name)
SELECT '1', 'ADMIN', 'admin'
    WHERE NOT EXISTS (SELECT 1 FROM role WHERE id = '1');

INSERT INTO role (id, code, name)
SELECT '2', 'USER', 'user'
    WHERE NOT EXISTS (SELECT 1 FROM role WHERE id = '2');
