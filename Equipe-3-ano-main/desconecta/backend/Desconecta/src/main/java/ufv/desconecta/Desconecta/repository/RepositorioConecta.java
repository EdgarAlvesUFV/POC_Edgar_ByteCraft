package ufv.desconecta.Desconecta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ufv.desconecta.Desconecta.model.Conecta;

import java.util.Optional;
@Repository
public interface RepositorioConecta extends JpaRepository<Conecta, Long> {

    @Query("SELECT c FROM Conecta c WHERE c.PK_Conecta = :id")
    Optional<Conecta> findByIdConecta(@Param("id") int id);

}
