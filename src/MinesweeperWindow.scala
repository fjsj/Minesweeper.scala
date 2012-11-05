import java.awt.BorderLayout
import java.awt.GridLayout
import java.awt.event.InputEvent
import java.util.Random
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JProgressBar
import javax.swing.JToggleButton
import javax.swing.JToolBar
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.WindowConstants
import javax.swing.JLabel
import javax.swing.ImageIcon
import java.awt.Color
import javax.swing.border.Border
import javax.swing.BorderFactory

class MinesweeperWindow extends JFrame {
  val rowsLvl1 = 9
  val columnsLvl1 = 9
  val bombsLvl1 = 9
  val rowsLvl2 = 12
  val columnsLvl2 = 12
  val bombsLvl2 = 30
  val rowsLvl3 = 16
  val columnsLvl3 = 16
  val bombsLvl3 = 70
  val intro = new IntroPanel()
  val body = new JPanel()
  val gamePanel = new JPanel()
  val toolBar = new JToolBar()
  val btnLevel1 = new JLabel()
  val btnLevel2 = new JLabel()
  val btnLevel3 = new JLabel()
  val newGame = new JLabel(new ImageIcon("img/nwGame.png"))
  val squareIcon = new ImageIcon("img/square.png")
  val flagIcon = new ImageIcon("img/flag.png")
  val bombIcon = new ImageIcon("img/bomb.png")
  val emptyIcon = new ImageIcon("img/empty.png")
  val bckColor = new Color(135, 199, 217)

  def defineGamePanel(game: Game) {
    val gridLayout = new GridLayout()
    gridLayout.setRows(game.rows)
    gridLayout.setColumns(game.columns)
    gamePanel.setLayout(gridLayout)
  }

  def initializeGamePanel(game: Game) {
    val gameButtons = List.tabulate(game.rows, game.columns)((_, _) => new JLabel(squareIcon))
    gamePanel.removeAll()
    gameButtons.flatten.map(button => gamePanel.add(button))
    validate()
    repaint()

    def updateGameState(game: Game) {
      gameButtons.flatten.map(button => {
        val panelX = (button.getX() / button.getSize().getWidth()).toInt
        val panelY = (button.getY() / button.getSize().getHeight()).toInt

        val cell = game.getCell(panelX, panelY)
        cell match {
          case Empty(true) => {
            button.setIcon(null)
            button.revalidate()
            button.setOpaque(true)
            button.setBackground(bckColor)
            button.setBorder(BorderFactory.createLineBorder(new Color(49, 133, 156)))
            button.setForeground(new Color(119, 147, 60))
          }
          case Hint(true, hint) => {
            button.setIcon(null)
            button.revalidate()
            button.setOpaque(true)
            button.setBackground(bckColor)
            button.setBorder(BorderFactory.createLineBorder(new Color(49, 133, 156)))
            button.setForeground(new Color(128, 100, 162))
            button.setText(hint.toString)
          }
          case Bomb(true) => {
            button.setIcon(bombIcon)
          }
          case _ => {}
        }

        button.addMouseListener(new MouseAdapter() {
          override def mouseReleased(e: MouseEvent) {
            showCell(button, panelX, panelY)
          }
        })
      })
    }
    updateGameState(game)
    validate()
    repaint()

    if (game.hasActiveBomb()) {
      JOptionPane.showInternalMessageDialog(body, "Game Over", "Bomb exploded!",
        JOptionPane.ERROR_MESSAGE)
      initializeGamePanel(new Game(game.rows, game.columns, game.bombs))
    } else if (game.hasOnlyBombs) {
      JOptionPane.showInternalMessageDialog(body, "You Won", "Congratulations, you won!",
        JOptionPane.INFORMATION_MESSAGE)
      initializeGamePanel(new Game(game.rows, game.columns, game.bombs))
    }

    def showCell(button: JLabel, panelX: Int, panelY: Int) {
      if (button.isEnabled()) {
        button.setEnabled(false)
        val gameUpdated = game.showCell(panelX, panelY)
        initializeGamePanel(gameUpdated)
      }
    }
  }

  def defineNewGame(game: Game) {
    newGame.addMouseListener(new MouseAdapter() {
      override def mouseReleased(e: MouseEvent) {
        initializeGamePanel(new Game(game.rows, game.columns, game.bombs))
      }
    })
  }

  def defineToolBar() {
    toolBar.setFloatable(false)
    toolBar.add(newGame)
  }

  def defineBody() {
    body.setLayout(new BorderLayout())
    body.add(gamePanel, BorderLayout.CENTER)
    body.add(toolBar, BorderLayout.NORTH)
  }

  def defineLevelButtons() {
    val icon1 = new ImageIcon("img/btn1.png")
    btnLevel1.setSize(icon1.getIconWidth, icon1.getIconHeight)
    btnLevel1.setIcon(icon1)
    btnLevel1.setLocation(100, 125)
    btnLevel1.addMouseListener(new MouseAdapter() {
      override def mouseReleased(e: MouseEvent) {
        val game = new Game(rowsLvl1, columnsLvl1, bombsLvl1)
        initializeLevel(game)
      }
    })
    val icon2 = new ImageIcon("img/btn2.png")
    btnLevel2.setSize(icon2.getIconWidth, icon2.getIconHeight)
    btnLevel2.setIcon(icon2)
    btnLevel2.setLocation(100, 183)
    btnLevel2.addMouseListener(new MouseAdapter() {
      override def mouseReleased(e: MouseEvent) {
        val game = new Game(rowsLvl2, columnsLvl2, bombsLvl2)
        initializeLevel(game)
      }
    })
    val icon3 = new ImageIcon("img/btn3.png")
    btnLevel3.setSize(icon3.getIconWidth, icon3.getIconHeight)
    btnLevel3.setIcon(icon3)
    btnLevel3.setLocation(100, 241)
    btnLevel3.addMouseListener(new MouseAdapter() {
      override def mouseReleased(e: MouseEvent) {
        val game = new Game(rowsLvl3, columnsLvl3, bombsLvl3)
        initializeLevel(game)
      }
    })
  }

  def initializeLevel(game: Game) {
    setSize(game.rows * (squareIcon.getIconWidth - 1), (game.columns * squareIcon.getIconHeight))
    getContentPane.removeAll()
    setContentPane(body)
    validate()
    repaint()
    defineNewGame(game)
    defineGamePanel(game)
    initializeGamePanel(game)
  }

  def defineIntro() {
    intro.setLayout(null)
    intro.add(btnLevel1)
    intro.add(btnLevel2)
    intro.add(btnLevel3)
  }

  defineLevelButtons()
  defineIntro()

  defineToolBar()
  defineBody()

  setSize(intro.imgWidth, intro.imgHeight)
  setContentPane(intro)
  setTitle("Minesweeper")
  setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
}

object GameMain {
  def main(args: Array[String]): Unit = {
    val window = new MinesweeperWindow()
    window.setVisible(true)
  }
}
