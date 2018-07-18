package com.myapp.validation.constraintvalidation;

import com.myapp.validation.constraints.Unique;

import javax.persistence.EntityManager;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * <p>Created by MontolioV on 18.07.18.
 */
public class UniqueValidator implements ConstraintValidator<Unique, String> {
    public static final String MESSAGE = "Value isn't unique, but must be unique!";

//    @PersistenceContext(unitName = "warehouse-api-pu")
    private EntityManager em;
    private Class<?> entityClass;
    private String queryName;
    private String queryParameterName;

    /**
     * Initializes the validator in preparation for
     * {@link #isValid(Object, ConstraintValidatorContext)} calls.
     * The constraint annotation for a given constraint declaration
     * is passed.
     * <p>
     * This method is guaranteed to be called before any use of this instance for
     * validation.
     * <p>
     * The default implementation is a no-op.
     *
     * @param constraintAnnotation annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(Unique constraintAnnotation) {
        System.out.println("!@# validator initialize");
        entityClass = constraintAnnotation.entityClass();
        queryName = constraintAnnotation.queryName();
        queryParameterName = constraintAnnotation.queryParameterName();
    }

    /**
     * Implements the validation logic.
     * The state of {@code value} must not be altered.
     * <p>
     * This method can be accessed concurrently, thread-safety must be ensured
     * by the implementation.
     *
     * @param value   object to validate
     * @param context context in which the constraint is evaluated
     * @return {@code false} if {@code value} does not pass the constraint
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        System.out.println("!@# validator em is null" + (em == null));

        return true;
//        return em.createNamedQuery(queryName, entityClass)
//                .setParameter(queryParameterName, value)
//                .getResultList()
//                .isEmpty();
    }
}
