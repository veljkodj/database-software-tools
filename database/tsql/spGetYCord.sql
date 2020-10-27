Create function spGetYCord(@idAddress int)
returns int
as
Begin
	return (Select y FROM [Address] WHERE IdAddress = @idAddress)
End