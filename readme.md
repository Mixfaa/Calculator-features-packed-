# Calculator
parses math input into a binary tree, operations are chained together. Has built in functions and constants
use MathParserBuilder to add custom functions and constatns

usage example:

```java
import com.mixfa.calculator.MathParser;
import com.mixfa.calculator.exception.MathParsingException;

void main() throws MathParsingException {
    var parser = MathParser.defaultParser();
    IO.println(parser.parse("sin(1)^2").calculate());
}
```

more complicated example

```java
import com.mixfa.calculator.*;
import com.mixfa.calculator.exception.MathParsingException;

import static com.mixfa.calculator.MathUtils.toValue;

void main() throws MathParsingException {
    var parser = new MathParserBuilder()
            .addDefaults()
            .addConstant(new MathConstant("x", toValue(BigInteger.valueOf(25))))
            .addFunction(new FunctionComponent.FunctionComponent0("rand", () -> MathUtils.toValue(BigDecimal.valueOf(Math.random()))))
            .addFunction(new FunctionComponent.FunctionComponent1("negate", (arg) -> arg.calculate().negate()))
            .build();

    IO.println(parser.parse("rand()+negate(x)").calculate());
    IO.println(parser.parse("2/2").calculate());
    IO.println(parser.parse("lcm(25,15)").calculate());
    IO.println(parser.parse("1-(-1)").calculate());
}
```
