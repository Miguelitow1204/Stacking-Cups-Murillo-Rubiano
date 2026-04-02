package tower;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import javax.swing.JOptionPane;
import shapes.Canvas;

/**
 * Acceptance tests that visually demonstrate the best features of the project.
 * Each test shows a simulation and asks the user to confirm acceptance.
 *
 * @author Murillo-Rubiano with support of GPT-4o
 * @version 1.0
 */
public class TowerATest {

    /**
     * Acceptance test 1: ICPC solver simulation.
     * Shows the TowerContest solver building a tower of n=4 cups
     * reaching exactly h=9 cm, with animated cup placement.
     */
    @Test
    public void testAcceptanceSolverSimulation() {
        //Show start message
        JOptionPane.showMessageDialog(null,
            "PRUEBA DE ACEPTACIÓN 1\n\n" +
            "Se mostrará el solver ICPC construyendo\n" +
            "una torre de 4 copas con altura exacta de 9 cm.\n\n" +
            "Observe cómo las copas se anidan y apilan\n" +
            "para alcanzar la altura objetivo.",
            "Prueba de Aceptación 1",
            JOptionPane.INFORMATION_MESSAGE);

        //run simulation
        TowerContest tc = new TowerContest();
        String solution = tc.solve(4, 9);
        assertNotEquals("impossible", solution);

        String[] heightsStr = solution.split(" ");
        Tower tower = new Tower(60, 11);

        for (String heightStr : heightsStr) {
            int cupId = (Integer.parseInt(heightStr) + 1) / 2;
            tower.pushCup(cupId);
            Canvas.getCanvas().wait(800);
        }

        //Check correct height
        assertEquals(9, tower.height());

        //ask the user
        int response = JOptionPane.showConfirmDialog(null,
            "¿La torre se construyó correctamente\n" +
            "alcanzando una altura de 9 cm?\n\n" +
            "Solución aplicada: " + solution,
            "¿Acepta la prueba?",
            JOptionPane.YES_NO_OPTION);

        assertEquals(JOptionPane.YES_OPTION, response,
            "El usuario no aceptó la prueba de simulación del solver.");
    }

    /**
     * Acceptance test 2: Special cup and lid types demonstration.
     * Shows the behavior of OpenerCup, HierarchicalCup and LockedLid
     * interacting in a single tower.
     */
    @Test
    public void testAcceptanceSpecialTypes() {
        //Show start message
        JOptionPane.showMessageDialog(null,
            "PRUEBA DE ACEPTACIÓN 2\n\n" +
            "Se demostrarán los tipos especiales de copas y tapas:\n\n" +
            "1. HierarchicalCup: se inserta en su posición correcta\n" +
            "2. LockedLid: bloquea la copa hasta ser removida\n" +
            "3. OpenerCup: elimina todas las tapas al entrar\n\n" +
            "Observe el comportamiento de cada elemento.",
            "Prueba de Aceptación 2",
            JOptionPane.INFORMATION_MESSAGE);

        Tower tower = new Tower(150, 30);

        //1. add normal cups
        JOptionPane.showMessageDialog(null, "Paso 1: Agregando copas normales 1 y 2...");
        tower.pushCup(1, "normal");
        Canvas.getCanvas().wait(800);
        tower.pushCup(2, "normal");
        Canvas.getCanvas().wait(800);

        //2. HierarchicalCup is inserted into the correct position
        JOptionPane.showMessageDialog(null, "Paso 2: Agregando HierarchicalCup 4...\nSe desplazará antes que las copas menores.");
        tower.pushCup(4, "hierarchical");
        Canvas.getCanvas().wait(800);
        assertTrue(tower.ok());

        //3. LockedLid locks its cup
        JOptionPane.showMessageDialog(null, "Paso 3: Agregando LockedLid sobre copa 2...\nBloqueará el popCup.");
        tower.pushLid(2, "locked");
        Canvas.getCanvas().wait(800);
        tower.popCup();
        assertFalse(tower.ok()); //locked
        Canvas.getCanvas().wait(800);

        //4. Remove the lid to unlock
        JOptionPane.showMessageDialog(null, "Paso 4: Removiendo LockedLid para desbloquear...");
        tower.popLid();
        Canvas.getCanvas().wait(800);

        //5. OpenerCup removes all lids
        JOptionPane.showMessageDialog(null, "Paso 5: Agregando tapas y luego OpenerCup...\nEliminará todas las tapas.");
        tower.pushLid(1, "normal");
        Canvas.getCanvas().wait(800);
        tower.pushLid(3, "normal");
        Canvas.getCanvas().wait(800);
        tower.pushCup(3, "opener");
        Canvas.getCanvas().wait(800);
        assertTrue(tower.ok());

        //Check that there are no lids
        String[][] items = tower.stackingItems();
        for (String[] item : items) {
            assertNotEquals("lid", item[0]);
        }

        //ask the user
        int response = JOptionPane.showConfirmDialog(null,
            "¿Los tipos especiales se comportaron correctamente?\n\n" +
            "- HierarchicalCup se insertó en su posición\n" +
            "- LockedLid bloqueó el popCup\n" +
            "- OpenerCup eliminó todas las tapas",
            "¿Acepta la prueba?",
            JOptionPane.YES_NO_OPTION);

        assertEquals(JOptionPane.YES_OPTION, response,
            "El usuario no aceptó la prueba de tipos especiales.");
    }
}