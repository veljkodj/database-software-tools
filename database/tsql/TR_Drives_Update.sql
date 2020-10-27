CREATE TRIGGER TR_Drives_Update
ON [dbo].[Drives]
INSTEAD OF UPDATE
AS
BEGIN

	Declare @new_idDrives int
	Declare @new_idUser int
	Declare @new_cost decimal(10,3)
	Declare @new_earnings decimal(10,3)
	Declare @new_currentAddress int
	Declare @new_idVehicle int

	Declare @old_idDrives int
	Declare @old_idUser int
	Declare @old_cost decimal(10,3)
	Declare @old_earnings decimal(10,3)
	Declare @old_currentAddress int
	Declare @old_idVehicle int

	Declare @MyCursorI Cursor
	Declare @MyCursorD Cursor

	SET @MyCursorI = CURSOR FOR
	select IdDrives, IdUser, Cost, Earnings, CurrentAddress, IdVehicle
	from inserted

	SET @MyCursorD = CURSOR FOR
	select IdDrives, IdUser, Cost, Earnings, CurrentAddress, IdVehicle
	from deleted

	OPEN @MyCursorI
	FETCH NEXT FROM @MyCursorI
	INTO @new_idDrives, @new_idUser, @new_cost, @new_earnings, @new_currentAddress, @new_idVehicle

	OPEN @MyCursorD
	FETCH NEXT FROM @MyCursorD
	INTO @old_idDrives, @old_idUser, @old_cost, @old_earnings, @old_currentAddress, @old_idVehicle

	WHILE @@FETCH_STATUS = 0
	BEGIN

		IF (
			@new_idDrives = @old_idDrives AND
			@new_idUser = @old_idUser AND
			@new_idVehicle = @old_idVehicle
		) BEGIN
			UPDATE Drives
			SET CurrentAddress = @new_currentAddress, Earnings = @new_earnings, Cost = @new_cost
			WHERE IdDrives = @new_idDrives
		END
			

		FETCH NEXT FROM @MyCursorI
		INTO @new_idDrives, @new_idUser, @new_cost, @new_earnings, @new_currentAddress, @new_idVehicle

		FETCH NEXT FROM @MyCursorD
		INTO @old_idDrives, @old_idUser, @old_cost, @old_earnings, @old_currentAddress, @old_idVehicle

	END

	CLOSE @MyCursorI
	DEALLOCATE @MyCursorI

	CLOSE @MyCursorD
	DEALLOCATE @MyCursorD

End