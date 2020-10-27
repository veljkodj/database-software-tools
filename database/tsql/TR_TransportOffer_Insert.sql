CREATE TRIGGER TR_TransportOffer_Insert
ON Package
FOR INSERT
AS
BEGIN

	Declare @id int
	Declare @type int
	Declare @weight decimal(10,3)
	Declare @startAddr int
	Declare @endAddr int

	Declare @startAddrX int
	Declare @startAddrY int
	Declare @endAddrX int
	Declare @endAddrY int

	Declare @distance decimal(10,3)
	Declare @price decimal(10,3)

	Declare @MyCursor Cursor

	SET @MyCursor = CURSOR FOR
	select IdPackage, Type, Weight, ReturnAddress, DeliveryAddress
	from inserted

	OPEN @MyCursor
	FETCH NEXT FROM @MyCursor
	INTO @id, @type, @weight, @startAddr, @endAddr

	WHILE @@FETCH_STATUS = 0
	BEGIN

		SET @startAddrX = dbo.spGetXCord (@startAddr)
		SET @startAddrY = dbo.spGetYCord (@startAddr)
		SET @endAddrX = dbo.spGetXCord (@endAddr)
		SET @endAddrY = dbo.spGetYCord (@endAddr)

		SET @distance = SQRT(SQUARE(@startAddrX-@endAddrX)+SQUARE(@startAddrY-@endAddrY))
		SET @price = @distance

		SET @price = 
		case
			when @type = 0 then @price*115 
			when @type = 1 then @price*(175 + @weight*100)
			when @type = 2 then @price*(250 + @weight*100) 
			when @type = 3 then @price*(350 + @weight*500) 
		end

		UPDATE Package
		set Price = @price
		where IdPackage = @id

		FETCH NEXT FROM @MyCursor
		INTO @id, @type, @weight, @startAddr, @endAddr

	END

	CLOSE @MyCursor
	DEALLOCATE @MyCursor

End