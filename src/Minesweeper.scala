import java.util.Random

abstract class Cell(shownc: Boolean) {
  val shown: Boolean = shownc
}

case class Bomb(override val shown: Boolean) extends Cell(shown)
case class Empty(override val shown: Boolean) extends Cell(shown)
case class Hint(override val shown: Boolean, val xc: Int) extends Cell(shown) {
  val x = xc
}

class Game(rowsc: Int, columnsc: Int, bombsc: Int, boardc: List[List[Cell]] = Nil) {
  val rows = rowsc
  val columns = columnsc
  val bombs = bombsc
  val board = if (boardc != Nil) boardc else initBoard()

  private def initBoard(): List[List[Cell]] = {
    val board1 = List.tabulate(rows)(_ => List.tabulate(columns)(_ => new Empty(false)))
    val board2 = initBombs(bombs, board1)
    initHints(board2, rows, columns)
  }

  //bombs
  private def initBombs(quantity: Int, board: List[List[Cell]]): List[List[Cell]] = {
    val newboard = tryToPutBomb(board)
    if (quantity > 1) {
      initBombs(quantity - 1, newboard)
    } else {
      newboard
    }
  }

  private def putBomb(x: Int, y: Int, board: List[List[Cell]]): List[List[Cell]] = {
    board.updated(x, board(x).updated(y, Bomb(false)))
  }

  private def tryToPutBomb(board: List[List[Cell]]): List[List[Cell]] = {
    val x = new Random().nextInt(rows)
    val y = new Random().nextInt(columns)
    val t: Cell = board(x)(y)
    t match {
      case Bomb(_) => tryToPutBomb(board)
      case _ => putBomb(x, y, board)
    }
  }
  //end of bombs

  //hints
  private def initHints(board: List[List[Cell]], x: Int, y: Int): List[List[Cell]] = {
    List.tabulate(x, y)((i, j) => transformIntoHint(board, i, j))
  }

  private def transformIntoHint(boardWithBombs: List[List[Cell]], x: Int, y: Int): Cell = {
    val cell = boardWithBombs(x)(y)
    cell match {
      case Bomb(b) => Bomb(b)
      case _ => initHint(boardWithBombs, x, y)
    }
  }

  private def initHint(boardWithBombs: List[List[Cell]], x: Int, y: Int): Cell = {
    def countBomb(cell: Cell): Int = {
      cell match {
        case Bomb(_) => 1
        case _ => 0
      }
    }

    val neighborCells = getCellOnBoard(boardWithBombs, x - 1, y - 1) ::
                        getCellOnBoard(boardWithBombs, x, y - 1) ::
                        getCellOnBoard(boardWithBombs, x + 1, y - 1) ::
                        getCellOnBoard(boardWithBombs, x - 1, y) ::
                        getCellOnBoard(boardWithBombs, x + 1, y) ::
                        getCellOnBoard(boardWithBombs, x - 1, y + 1) ::
                        getCellOnBoard(boardWithBombs, x, y + 1) ::
                        getCellOnBoard(boardWithBombs, x + 1, y + 1) :: Nil
    val hintValue = (neighborCells.map(countBomb)).foldLeft(0)(_ + _)
    hintValue match {
      case 0 => Empty(false)
      case _ => Hint(false, hintValue)
    }
  }
  //end of hints

  //game behavior
  def showCell(tuple: (Int, Int)): Game = {
    val x = tuple._1
    val y = tuple._2
    val cell = board(x)(y)
    cell match {
      case Empty(false) => showNeighborCells(x, y)
      case Bomb(false) => {
        val newboard = board.updated(x, board(x).updated(y, Bomb(true)))
        new Game(rows, columns, bombs, newboard)
      }
      case Hint(false, hint) => {
        val newboard = board.updated(x, board(x).updated(y, Hint(true, hint)))
        new Game(rows, columns, bombs, newboard)
      }
      case _ => new Game(rows, columns, bombs, board)
    }
  }

  private def showNeighborCells(x: Int, y: Int): Game = {
    def getPosition(board: List[List[Cell]], x: Int, y: Int): List[(Int, Int)] = {
      if (contains(x, y)) {
        List(Tuple2(x, y))
      } else {
        Nil
      }
    }

    val newboard = board.updated(x, board(x).updated(y, Empty(true)))
    val newGame = new Game(rows, columns, bombs, newboard)
    val neighborPositions: List[(Int, Int)] = getPosition(board, x - 1, y - 1) ++
                                              getPosition(board, x, y - 1) ++
                                              getPosition(board, x + 1, y - 1) ++
                                              getPosition(board, x - 1, y) ++
                                              getPosition(board, x + 1, y) ++
                                              getPosition(board, x - 1, y + 1) ++
                                              getPosition(board, x, y + 1) ++
                                              getPosition(board, x + 1, y + 1) ++ Nil
    neighborPositions.foldLeft(newGame)((game, tuple) => game.showCell(tuple))
  }

  def hasOnlyBombs(): Boolean = {
    !board.flatten.exists(cell => cell match {
      case Empty(false) => true
      case Hint(false, _) => true
      case _ => false
    })
  }

  def hasActiveBomb(): Boolean = {
    board.flatten.exists(cell => cell match {
      case Bomb(true) => true
      case _ => false
    })
  }

  def contains(x: Int, y: Int): Boolean = {
    x >= 0 && x < rows && y >= 0 && y < columns
  }

  def getCell(x: Int, y: Int): Cell = {
    if (contains(x, y)) {
      board(x)(y)
    } else {
      null
    }
  }
  
  def getCellOnBoard(board: List[List[Cell]], x: Int, y: Int): Cell = {
    if (contains(x, y)) {
      board(x)(y)
    } else {
      null
    }
  }
}
