package projet.td3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class TransactionConfig {

    /**
     * Chaque tentative de retry doit être sa propre transaction physique (REQUIRES_NEW) :
     * si on réutilise la transaction de l'appelant, un échec de verrouillage optimiste
     * invalide toute la transaction englobante et empêche un retry propre.
     */
    @Bean
    public TransactionTemplate retryTransactionTemplate(PlatformTransactionManager transactionManager) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return template;
    }
}
