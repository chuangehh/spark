grammar Calculator;

// 表达式 -> 文件末尾
line : expr EOF ;

expr : '(' expr ')'         # parentExpr
     | expr ('*'|'/') expr  # multOrDiv
     | expr ('+'|'-') expr  # addOrSubstract
     | FLOAT                # float;

// word split
WS : [ \t\n\r]+ -> skip;

// DIGIT 一个或多个,  '.'DIGIT 0个或多个,  EXPONENT 一个或0个
FLOAT : DIGIT + '.' DIGIT* EXPONENT?
      // '.' DIGIT+ 一个或多个,  EXPONENT 一个或0个
      | '.' DIGIT+ EXPONENT?
      // DIGIT+ 一个或多个,  EXPONENT 一个或0个
      | DIGIT+ EXPONENT? ;

// [0-9] 0到9
fragment DIGIT : '0'..'9';

// 科学计数法
fragment EXPONENT : ('e'|'E') ('+'|'-')? DIGIT+;