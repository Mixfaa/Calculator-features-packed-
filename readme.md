# Calculator
parses math input into a binary tree, operations are chained together. Has built in functions and constants
use MathParserBuilder to add custom functions and constatns

usage example:

```java
import com.mixfa.calculator.MathParser;
import com.mixfa.calculator.exception.MathParsingException;

void main() throws MathParsingException {
    var parser = MathParser.defaultParser;
    IO.println(parser.parse("sin(1)^2").calculate());
}
```

more complicated example
```java
import com.mixfa.calculator.FunctionComponent;
import com.mixfa.calculator.MathComponent;
import com.mixfa.calculator.MathConstant;
import com.mixfa.calculator.MathParserBuilder;
import com.mixfa.calculator.exception.MathParsingException;

void main() throws MathParsingException {
    var parser = new MathParserBuilder()
            .addDefaults()
            .addConstant(new MathConstant("x", new MathComponent.Value(BigDecimal.valueOf(25.0))))
            .addFunction(new FunctionComponent.FunctionComponent0("rand", () -> new MathComponent.Value(BigDecimal.valueOf(Math.random()))))
            .addFunction(new FunctionComponent.FunctionComponent1("negate", (arg) -> new MathComponent.Value(arg.calculate().negate())))
            .build();

    IO.println(parser.parse("rand()+negate(x)").calculate());
}
```
