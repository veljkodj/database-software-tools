CREATE TRIGGER TR_TransportOffer_Update
ON Package
FOR UPDATE
AS
BEGIN

	Declare @startAddrX int
	Declare @startAddrY int
	Declare @endAddrX int
	Declare @endAddrY int
	Declare @distance decimal(10,3)
	Declare @price decimal(10,3)

	Declare @new_id int
	Declare @new_status int
	Declare @new_type int
	Declare @new_weight decimal(10,3)
	Declare @new_startAddr int
	Declare @new_endAddr int

	Declare @old_id int
	Declare @old_status int
	Declare @old_type int
	Declare @old_weight decimal(10,3)
	Declare @old_startAddr int
	Declare @old_endAddr int

	Declare @MyCursorI Cursor
	Declare @MyCursorD Cursor

	SET @MyCursorD = CURSOR FOR
	select IdPackage, Status, Type, Weight, ReturnAddress, DeliveryAddress
	from deleted

	SET @MyCursorI = CURSOR FOR
	select IdPackage, Status, Type, Weight, ReturnAddress, DeliveryAddress
	from inserted

	OPEN @MyCursorD
	FETCH NEXT FROM @MyCursorD
	INTO @old_id, @old_status, @old_type, @old_weight, @old_startAddr, @old_endAddr

	OPEN @MyCursorI
	FETCH NEXT FROM @MyCursorI
	INTO @new_id, @new_status, @new_type, @new_weight, @new_startAddr, @new_endAddr

	WHILE @@FETCH_STATUS = 0
	BEGIN

		IF (@old_status != 0 AND (@old_type != @new_type OR @old_weight != @new_weight)) BEGIN
			ROLLBACK TRANSACTION
			BREAK
		END

		SET @startAddrX = dbo.spGetXCord (@new_startAddr)
		SET @startAddrY = dbo.spGetYCord (@new_startAddr)
		SET @endAddrX = dbo.spGetXCord (@new_endAddr)
		SET @endAddrY = dbo.spGetYCord (@new_endAddr)

		SET @distance = SQRT(SQUARE(@startAddrX-@endAddrX)+SQUARE(@startAddrY-@endAddrY))
		SET @price = @distance

		SET @price = 
		case
			when @new_type = 0 then @price*115 
			when @new_type = 1 then @price*(175 + @new_weight*100)
			when @new_type = 2 then @price*(250 + @new_weight*100) 
			when @new_type = 3 then @price*(350 + @new_weight*500) 
		end

		UPDATE Package
		set Price = @price
		where IdPackage = @new_id

		FETCH NEXT FROM @MyCursorD
		INTO @old_id, @old_status, @old_type, @old_weight, @old_startAddr, @old_endAddr

		FETCH NEXT FROM @MyCursorI
		INTO @new_id, @new_status, @new_type, @new_weight, @new_startAddr, @new_endAddr

	END

	CLOSE @MyCursorI
	DEALLOCATE @MyCursorI

	CLOSE @MyCursorD
	DEALLOCATE @MyCursorD

End