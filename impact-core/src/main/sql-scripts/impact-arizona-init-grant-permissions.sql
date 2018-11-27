CREATE LOGIN [impact] WITH PASSWORD = '<IMPACT_PASSWORD>',DEFAULT_DATABASE = [<DATABASE_NAME>]
GO

CREATE USER [impact] FOR LOGIN [impact] WITH DEFAULT_SCHEMA=[dbo]
GRANT CONTROL ON Schema :: [dbo] TO impact
GO

CREATE LOGIN [impact-portal] WITH PASSWORD = '<IMPACT_PORTAL_PASSWORD>'
GO

CREATE USER [impact-portal] FOR LOGIN [impact-portal] WITH DEFAULT_SCHEMA=[staging]
GRANT CONTROL ON Schema :: [staging] TO [impact-portal]
GRANT SELECT ON Schema :: [dbo] TO [impact-portal]
GRANT EXECUTE ON dbo.F_ALL_APPS TO [impact-portal]
GRANT EXECUTE ON dbo.F_ALL_REASONS TO [impact-portal]
GRANT EXECUTE ON dbo.F_LIST_NAICS_CODES TO [impact-portal]
GO
