package core.repository;

import java.util.List;
import java.util.Optional;

import core.model.Account;

/**
 * Repository interface for Account operations
 */
public interface AccountRepository {
    
    /**
     * Save or update an account
     * @param account account to save
     * @return saved account with ID
     */
    Account save(Account account);
    
    /**
     * Find account by ID
     * @param id account ID
     * @return account if found
     */
    Optional<Account> findById(Long id);
    
    /**
     * Find account by email
     * @param email email address
     * @return account if found
     */
    Optional<Account> findByEmail(String email);
    
    /**
     * Find all active accounts
     * @return list of active accounts
     */
    List<Account> findAllActive();
    
    /**
     * Update last login time
     * @param accountId account ID
     */
    void updateLastLogin(Long accountId);
    
    /**
     * Delete account by ID
     * @param id account ID
     * @return true if deleted
     */
    boolean deleteById(Long id);
    
    /**
     * Check if email exists
     * @param email email address
     * @return true if exists
     */
    boolean existsByEmail(String email);
}
