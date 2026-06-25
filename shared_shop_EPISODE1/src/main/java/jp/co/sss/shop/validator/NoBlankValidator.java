package jp.co.sss.shop.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoBlankValidator implements ConstraintValidator<NoBlank, String> {
	    @Override
	    public boolean isValid(String value, ConstraintValidatorContext context) {

	        // null は @NotNull で別途チェックする想定
	        if (value == null) {
	            return true;
	        }

	        // 全角スペースを半角スペースに置換して trim
	        String normalized = value.replace("　", " ").trim();

	        return !normalized.isEmpty();
	    }
}
