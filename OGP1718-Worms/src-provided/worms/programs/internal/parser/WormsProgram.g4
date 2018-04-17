// ANTLR v4 Grammar for controling Worms.
// Check the tutorial at http://jnb.ociweb.com/jnb/jnbJun2008.html
// or http://www.antlr.org/ for details.


grammar WormsProgram;

@header {
    package worms.programs.internal.parser.generated;
}


// ------------------------------------------------------------------------
// --- Eval and Related Definitions ---------------------------------------
// ------------------------------------------------------------------------
program:	(procdef+=proceduredef)*
            (programBody+=statement)*
;

proceduredef: DEF procname=IDENTIFIER COLON?
               body=statement
;

statement:    assignmentStatement
            | whileStatement
            | ifStatement    
            | printStatement
            | sequenceStatement
            | invokeStatement                    
            | breakStatement
            | actionStatement
; 

assignmentStatement:   variableName=IDENTIFIER ASSIGN value=expression ';'
;

whileStatement:    WHILE condition=expression COLON?
                      body=statement
;

ifStatement: IF condition=expression COLON?
                ifbody=statement
            (ELSE COLON?
            	elsebody=statement)?
;

printStatement: PRINT value=expression ';'
;

sequenceStatement: LEFT_BRACE (stmts+=statement)* RIGHT_BRACE
; 

invokeStatement: INVOKE procName=IDENTIFIER ';'
;


breakStatement: BREAK ';'
;


actionStatement:  TURN angle=expression ';' #turnAction
                | MOVE ';' #moveAction
                | JUMP ';' #jumpAction
                | EAT ';' #eatAction
                | FIRE ';' #fireAction
;

// order here sets order of operations (important for left-recursive expressions!)
expression:   variable=IDENTIFIER #readVariableExpression
            | value=NUMBER #constantExpression
            | TRUE #trueLiteralExpression
            | FALSE #falseLiteralExpression
            | NULL #nullExpression
            | SELF #selfExpression            
            | LEFT_PAREN subExpr=expression RIGHT_PAREN #parenExpression
            | left=expression op=(MUL | DIV) right=expression #mulDivExpression
            | left=expression op=(ADD | SUB) right=expression #addSubExpression
            | left=expression op=(LT | LTE | GT | GTE | EQ | NEQ) right=expression #comparisonExpression
            | left=expression op=(AND | OR) right=expression #andOrExpression
            | SQRT LEFT_PAREN expr=expression RIGHT_PAREN #sqrtExpression
            | SIN LEFT_PAREN expr=expression RIGHT_PAREN #sinExpression
            | COS LEFT_PAREN expr=expression RIGHT_PAREN #cosExpression
            | NOT expr=expression  #notExpression
            | GETX expr=expression #getXExpression
            | GETY expr=expression #getYExpression
            | GETRADIUS expr=expression #getRadiusExpression
            | GETDIR expr=expression #getDirectionExpression
            | GETAP expr=expression #getAPExpression
            | GETMAXAP expr=expression #getMaxAPExpression
            | GETHP expr=expression #getHPExpression
            | SAMETEAM expr=expression #sameTeamExpression
            | SEARCHOBJ expr=expression #searchObjExpression
            | DISTANCE expr=expression #distanceExpression
            | ISWORM expr=expression #isWormExpression
            | ISFOOD expr=expression #isFoodExpression
            | ISPROJECTILE expr=expression #isProjectileExpression
;

// ------------------------------------------------------------------------
// --- Specifiers -----------------------------------------------
// ------------------------------------------------------------------------

NULL:       'null';
SELF:       'self';
TRUE:       'true';
FALSE:      'false';

// ------------------------------------------------------------------------
// --- Unary Operations ---------------------------------------------------
// ------------------------------------------------------------------------
SQRT:      'sqrt';
SIN:       'sin';
COS:       'cos';
NOT:       '!';
GETX:      'getx';
GETY:      'gety';
GETRADIUS: 'getradius';
GETDIR:    'getdir';
GETAP:     'getap';
GETMAXAP:  'getmaxap';
GETHP:     'gethp';
SAMETEAM:  'sameteam';
SEARCHOBJ: 'searchobj';
DISTANCE:  'distance';
ISWORM:    'isworm';
ISFOOD:    'isfood';
ISPROJECTILE: 'isprojectile';

// ------------------------------------------------------------------------
// --- Actions -----------------------------------------------
// ------------------------------------------------------------------------
TURN:      'turn';
MOVE:      'move';
JUMP:      'jump';
EAT:       'eat';
FIRE:      'fire'; 


// ------------------------------------------------------------------------
// --- Control Flow -------------------------------------------------------
// ------------------------------------------------------------------------
DEF:       'def';
IF:        'if';
INVOKE:    'invoke';
THEN:      'then';
ELSE:      'else';
WHILE:     'while';
BREAK:     'break';
PRINT:     'print';


// ------------------------------------------------------------------------
// --- Assignment and Arithmetics -----------------------------------------
// ------------------------------------------------------------------------
ASSIGN:   ':=';
ADD:      '+';
SUB:      '-';
MUL:      '*';
DIV:      '/';
AND:      '&&';
OR:       '||';
LT:       '<';
LTE:      '<=';
GT:       '>';
GTE:      '>=';
EQ:       '==';
NEQ:      '!=';

// ------------------------------------------------------------------------
// --- Literals and Variables ---------------------------------------------
// ------------------------------------------------------------------------

NUMBER:  INTEGER | FLOAT;
FLOAT:   INTEGER '.' ('0'..'9')+;
INTEGER: ('-'|'+')? ('0'..'9')+;

IDENTIFIER: LETTER (LETTER | DIGIT | '_')*;
fragment LETTER: LOWER | UPPER;
fragment LOWER: 'a'..'z';
fragment UPPER: 'A'..'Z';
fragment DIGIT: '0'..'9';

LEFT_PAREN: '(';
RIGHT_PAREN: ')';
LEFT_BRACE: '{';
RIGHT_BRACE: '}';
COLON: ':';

// ------------------------------------------------------------------------
// --- Syntactical Ballast ------------------------------------------------
// ------------------------------------------------------------------------

// Skip runs of newline, space and tab characters.
WHITESPACE: [ \t\r\n]+ -> skip;
 
// Single-line comments begin with //, are followed by any characters
// other than those in a newline, and are terminated by newline characters.
SINGLE_COMMENT: '//' ~('\r' | '\n')* NEWLINE -> skip;
fragment NEWLINE: ('\r'? '\n')+;

