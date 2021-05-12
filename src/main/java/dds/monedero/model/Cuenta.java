package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {
  private double saldoInicial;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta(double montoInicial, List<Movimiento> movimientos) {
    this.saldoInicial = montoInicial;
    if (movimientos != null)
      this.movimientos = movimientos;
  }

  public void poner(double cuanto) {
    this.hayMontoNegativo(cuanto);
    this.seSuperoCantMaxDepositos();
    this.movimientos.add(new Movimiento(LocalDate.now(), cuanto, true));
  }

  private void hayMontoNegativo(double cuanto) {
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
  }

  private void seSuperoCantMaxDepositos() {
    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
  }

  public void sacar(double cuanto) {
    this.hayMontoNegativo(cuanto);
    this.fondosInsuficientes(cuanto);
    this.maximaExtraccionDiaria(cuanto);
    this.movimientos.add(new Movimiento(LocalDate.now(), cuanto, false));
  }

  private void fondosInsuficientes(double cuanto) {
    if (this.getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
  }

  private void maximaExtraccionDiaria(double cuanto) {
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy;
    if (cuanto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> movimiento.fueExtraido(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return this.sacarMontoSegun(true) - sacarMontoSegun(false);
  }

  private double sacarMontoSegun(Boolean depositado) {
    return this.movimientos.stream().filter(movimiento -> movimiento.isDeposito() == depositado).mapToDouble(Movimiento::getMonto).sum();
  }
}
