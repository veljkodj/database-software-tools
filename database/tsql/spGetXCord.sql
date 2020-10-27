Create function spGetXCord(@idAddress int)
returns int
as
Begin
	return (Select x FROM [Address] WHERE IdAddress = @idAddress)
End