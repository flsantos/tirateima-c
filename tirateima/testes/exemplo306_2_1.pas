program exemplo306_2_1;

var
  nivel: integer;

begin
  writeln('qual o nivel do pais?');
  readln(nivel);
  case nivel of
    1 : writeln('investimento pessimo');
    2 : writeln('investimento ruim');
    3 : writeln('investimento razoavel');
    4 : writeln('investimento bom');
    5 : writeln('investimento otimo');
  end;
end.
