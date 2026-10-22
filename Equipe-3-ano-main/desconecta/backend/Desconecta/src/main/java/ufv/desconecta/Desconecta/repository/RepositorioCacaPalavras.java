package ufv.desconecta.Desconecta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ufv.desconecta.Desconecta.model.CacaPalavras;

import java.util.Optional;
@Repository
public interface RepositorioCacaPalavras extends JpaRepository<CacaPalavras, Long> {
    @Query("SELECT c FROM CacaPalavras c WHERE c.PK_CacaPalavras = :id")
    Optional<CacaPalavras> findByIdCacaPalavras(@Param("id") int id);

}
