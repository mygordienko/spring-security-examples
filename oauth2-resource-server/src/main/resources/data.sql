INSERT INTO interviews (timestamp, manager_email, applicant_email, result)
SELECT '2026-04-15T09:00:00+03:00', 'manager1@company.test', 'applicant1@company.test', 'ACCEPTED'
WHERE NOT EXISTS (SELECT 1 FROM interviews);

INSERT INTO interviews (timestamp, manager_email, applicant_email, result)
SELECT '2026-04-15T11:30:00+03:00', 'manager2@company.test', 'applicant2@company.test', 'FAILED'
WHERE (SELECT COUNT(*) FROM interviews) = 1;

INSERT INTO demands (timestamp, author_email, type)
SELECT '2026-04-16T10:15:00+03:00', 'manager1@company.test', 'PAYMENT'
WHERE NOT EXISTS (SELECT 1 FROM demands);

INSERT INTO demands (timestamp, author_email, type)
SELECT '2026-04-16T14:45:00+03:00', 'manager2@company.test', 'STRUCTURE_CHANGE'
WHERE (SELECT COUNT(*) FROM demands) = 1;
