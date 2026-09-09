-- Demo data for course status coverage.
-- It is idempotent for the current gym_db schema.

START TRANSACTION;

INSERT INTO class_record (member_id, class_id, status, total_sessions, completed_sessions, rating)
SELECT v.member_id, v.class_id, v.status, 1, v.completed_sessions, v.rating
FROM (
    SELECT 7 AS member_id, 2 AS class_id, 0 AS status, 0 AS completed_sessions, NULL AS rating
    UNION ALL SELECT 8, 2, 0, 0, NULL
    UNION ALL SELECT 9, 2, 1, 1, 8.8
    UNION ALL SELECT 12, 2, 1, 1, 9.1
    UNION ALL SELECT 3, 4, 0, 0, NULL
    UNION ALL SELECT 4, 4, 1, 1, 8.4
) v
WHERE NOT EXISTS (
    SELECT 1
    FROM class_record cr
    WHERE cr.member_id = v.member_id
      AND cr.class_id = v.class_id
);

INSERT INTO class_table (
    class_id,
    class_name,
    coach_id,
    class_duration,
    class_level,
    max_capacity,
    class_description,
    price
)
SELECT
    16,
    '体态评估',
    4,
    45,
    1,
    12,
    '基础体态筛查与训练建议',
    59.00
WHERE NOT EXISTS (
    SELECT 1
    FROM class_table
    WHERE class_id = 16
);

COMMIT;
