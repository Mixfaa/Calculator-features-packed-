import com.mixfa.calculator.FunctionComponent;
import com.mixfa.calculator.MathConstant;
import com.mixfa.calculator.MathParserBuilder;
import com.mixfa.calculator.MathUtils;
import com.mixfa.calculator.exception.MathParsingException;

import static com.mixfa.calculator.MathUtils.toValue;

void main() throws MathParsingException {
    var parser = new MathParserBuilder()
            .addDefaults()
            .addConstant(new MathConstant("x", toValue(25)))
            .addFunction(new FunctionComponent.FunctionComponent0("rand", () -> MathUtils.toValue(Math.random())))
            .addFunction(new FunctionComponent.FunctionComponent1("negate", (arg) -> arg.calculate().negate()))
            .build();

//    IO.println(parser.parse("rand()+negate(x)").calculate());
//    IO.println(parser.parse("1/2").calculate());
//    IO.println(parser.parse("lcm(25,15)").calculate());
//    IO.println(parser.parse("1-(-1)").calculate());

    IO.println(parser.parse("gcd(4,6)").calculate());
    IO.println(parser.parse("(1/3)+2").calculate());
    IO.println(parser.parse("(1/3)+(2/6)").calculate());
//    IO.println(parser.parse("(1/3)").calculate());
//    IO.println(parser.parse("gcd(3,6)").calculate());


}
