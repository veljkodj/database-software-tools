CREATE TRIGGER TR_Drives_Insert
ON [dbo].[Drives]
INSTEAD OF INSERT
AS
BEGIN

	Declare @idDrives int
	Declare @idUser int
	Declare @cost decimal(10,3)
	Declare @earnings decimal(10,3)
	Declare @currentAddress int
	Declare @idVehicle int

	Declare @MyCursor Cursor

	SET @MyCursor = CURSOR FOR
	select IdDrives, IdUser, Cost, Earnings, CurrentAddress, IdVehicle
	from inserted

	OPEN @MyCursor
	FETCH NEXT FROM @MyCursor
	INTO @idDrives, @idUser, @cost, @earnings, @currentAddress, @idVehicle

	WHILE @@FETCH_STATUS = 0
	BEGIN

		IF NOT EXISTS(
			SELECT *
			FROM Drives
			WHERE IdUser = @idUser OR IdVehicle = @idVehicle
		)
			INSERT INTO Drives (IdUser, Cost, Earnings, CurrentAddress, IdVehicle)
			VALUES (@idUser, @cost, @earnings, @currentAddress, @idVehicle)

		FETCH NEXT FROM @MyCursor
		INTO @idDrives, @idUser, @cost, @earnings, @currentAddress, @idVehicle

	END

	CLOSE @MyCursor
	DEALLOCATE @MyCursor

End