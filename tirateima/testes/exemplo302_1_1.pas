program exemplo302_1_1;

var
  passaporte : char;

begin
  writeln('Voce tem passaporte?');
  readln(passaporte);
  if not (passaporte='s')
    then
      writeln('Voce nao pode trabalhar no pais.');
end.
