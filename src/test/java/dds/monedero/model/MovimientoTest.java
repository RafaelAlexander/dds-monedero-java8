package dds.monedero.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MovimientoTest {

  @Test
  public void crearMovimientoDepositado() {
    assertTrue(new Movimiento(LocalDate.now(), 200, true).isDeposito());
  }

  @Test
  public void crearMovimientoExtraido() {
    assertTrue(new Movimiento(LocalDate.now(), 200, false).isExtraccion());
  }
}
