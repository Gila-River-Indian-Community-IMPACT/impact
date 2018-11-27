USE [master]  
GO  

EXEC master.dbo.sp_addlinkedserver   
    @server = N'<VES_SERVER>',   
    @srvproduct=N'SQL Server' ;  
GO

EXEC master.dbo.sp_addlinkedsrvlogin   
    @rmtsrvname = N'<VES_SERVER>',   
	@rmtuser = N'<VES_LOGIN>',
	@rmtpassword = N'<VES_PASSWORD>',
    @useself = N'False' ;  
GO

