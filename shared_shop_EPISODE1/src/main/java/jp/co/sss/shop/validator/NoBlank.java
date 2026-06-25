package jp.co.sss.shop.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;


@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = NoBlankValidator.class)
public @interface NoBlank {

	    String message() default "空白のみの入力はできません";

	    Class<?>[] groups() default {};

	    Class<? extends Payload>[] payload() default {};
}



