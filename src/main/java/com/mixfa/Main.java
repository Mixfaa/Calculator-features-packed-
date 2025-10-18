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
