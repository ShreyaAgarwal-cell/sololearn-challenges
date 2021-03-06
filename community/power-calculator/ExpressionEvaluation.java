/* Power Calculator

The class calculate an expression with integers.
Priority of operations are supported as well as ().
Operations are: +,-,*,/, % for modulo and ^ for power.

Input: any arithmetic expression like: 5 + 7*3-12/(2 + 2*2)  
In case of empty imput the test cases will be printed.

Please vote if you like the code.
*/

import java.util.*;
import java.util.regex.Pattern;

public class ExpressionEvaluation {
    public static void main(String[] args) {
        final var sc = new Scanner(System.in);
        final var expression = sc.nextLine();
        sc.close();
        
        if(expression.isEmpty()) {
            runTests();
        } else {
            new PowerCalculator(expression).verbose().spaces().printResult();
        }
    }

    static void runTests() {
        System.out.println("Printing test expresions:");
        for (var expression : new String[] 
            { "2+2", "2*(4-5)", "4^7-12^3",
              "3^4+6*((9+12/6)*(24/2^3-1))"
            }) {
            var calc = new PowerCalculator(expression).spaces();
            calc.printResult();
        }
    }
}

class PowerCalculator {
    
    @FunctionalInterface
    private interface ArithmeticOperation {
        long calculate(long x, long y);
    }
    
    private static final List<Pattern> PATTERNS;
    private static final Map<Character, ArithmeticOperation> OPERATIONS;
    private static final String PARENTHESES = 
        "(?<before>.*?)\\((?<inside>[^()]+)\\)(?<after>.*)";
    private static final String EXPRESSION = 
        "(?<before>.*?)(?<x>(^-)?\\d+)(?: *)(?<op>[%s])(?: *)(?<y>-?\\d+)(?<after>.*)";
    
    static {
        final String[] priority = new String[] {"\\^", "*/%", "+-"};
        
        PATTERNS = new ArrayList<Pattern>();
        PATTERNS.add(Pattern.compile(PARENTHESES));
        for (final var operations : priority) {
            PATTERNS.add(Pattern.compile(String.format(EXPRESSION, operations)));
        }

        OPERATIONS = Map.of(
            '+', (a, b) -> a + b, 
            '-', (a, b) -> a - b,
            '*', (a, b) -> a * b, 
            '/', (a, b) -> a / b, 
            '%', (a, b) -> a % b,
            '^', (a, b) -> (long) Math.pow(a, b));
    }

    long calculate(String expression) {
        if (isVerbose) System.out.println(expression);
        
        for (final var pattern : PATTERNS) {
            final var m = pattern.matcher(expression);
            if (!m.matches()) continue;
            
            final long result;
            final var isParentheses = m.groupCount() == 3;
            
            if (isParentheses) {
                result = calculate(m.group("inside"));
            } else {
                final var op = m.group("op").charAt(0);
                final var x = Long.parseLong(m.group("x"));
                final var y = Long.parseLong(m.group("y"));
                    
                result = OPERATIONS.get(op).calculate(x, y);
            }
            return calculate(m.group("before") + result + m.group("after"));
        }
        return Long.parseLong(expression);
    }
    
    private String expression;
    private boolean isVerbose;

    PowerCalculator(String expression) {
        this.expression = expression;
    }

    PowerCalculator() {
        this.expression = "";
    }
    
    long calculate() {
        return calculate(this.expression);
    }

    PowerCalculator verbose() {
        this.isVerbose = true;
        return this;
    }

    PowerCalculator silent() {
        this.isVerbose = false;
        return this;
    }

    PowerCalculator compact() {
        expression = expression.replaceAll(" +", "");
        return this;
    }

    PowerCalculator spaces() {
        this.expression = this.expression
                .replaceAll("(?<=[\\d()]) *([-+%*/^&|])", " $1")
                .replaceAll("([-+%*/^&|]) *(?=[\\d()])", "$1 ");
        return this;
    }

    public String getExpression() {
        return this.expression;
    }

    public PowerCalculator setExpression(String expression) {
        this.expression = expression;
        return this;
    }
    
    @Override
    public String toString() {
        return this.expression + " = " + this.calculate();
    }

    public void printResult() {
        System.out.println(this);
    }
}
