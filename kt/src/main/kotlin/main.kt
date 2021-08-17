import kotlinx.browser.document
import kotlinx.browser.localStorage
import org.w3c.dom.asList
import kotlin.js.Json
import kotlin.js.json

const val STORAGE_KEY = "books"

data class Book(
  val id: String,
  val state: BookState,
  val year: String,
  val imageUrl: String,
  val name: String,
  val holder: String?
)

enum class BookState(
  val bookStateCssClass: String = "",
  val btnStateCssClass: String = "",
) {
  Available,
  Taken(
    bookStateCssClass = "book--on-hands",
    btnStateCssClass = "btn--maroon",
  ),
  Soon(
    bookStateCssClass = "book--soon",
    btnStateCssClass = "btn--gray",
  ),
}

fun renderBookHtml(book: Book): String {
  return """
  |<div class="book-list__item book ${book.state.bookStateCssClass}">
  |  <img class="book__image" src="${book.imageUrl}" />
  |  <div class="book__info">
  |    <p class="book__year">${book.year}</p>
  |    <p class="book__name">${book.name}</p>
  |    <button data-book-btn="${book.id}" class="book__btn btn btn--text ${book.state.btnStateCssClass}">${
    getBookButtonCaption(
      book
    )
  }</button>
  |  </div>
  |</div>
""".trimMargin()
}

fun getBookButtonCaption(book: Book): String {
  return when (book.state) {
    BookState.Available -> "Взять книгу"
    BookState.Taken -> {
      if (book.holder != null) "Вернуть"
      else "Узнать у кого"
    }
    BookState.Soon -> "Скоро"
  }
}

fun render(books: List<Book>) {
  val content = document.getElementById("booksList") ?: return
  content.innerHTML = ""
  books.forEach { book ->
    content.insertAdjacentHTML("beforeend", renderBookHtml(book))
  }
  val nodes = document.querySelectorAll("[data-book-btn]")
  nodes.asList().forEach { node ->
    node.addEventListener("click", {
      val bookId = it.target.asDynamic().getAttribute("data-book-btn")
      saveBooks(getBooks().map { book ->
        if (book.id == bookId && book.state != BookState.Soon) {
          book.copy(state = if (book.state == BookState.Available) BookState.Taken else BookState.Available)
        } else {
          book
        }
      })
      render(getBooks())
    })
  }
}

fun parseBookJson(json: String): List<Book> {
  val books = JSON.parse<Json>(json).unsafeCast<Array<Json>>()
  return books.map { map ->
    Book(
      id = map["id"].toString(),
      state = BookState.valueOf(map["state"].toString()),
      year = map["year"].toString(),
      imageUrl = map["imageUrl"].toString(),
      name = map["name"].toString(),
      holder = map["holder"].toString(),
    )
  }
}

fun encodeBookJson(books: List<Book>): String {
  return JSON.stringify(books.map { book ->
    json(
      "id" to book.id,
      "state" to book.state.name,
      "year" to book.year,
      "imageUrl" to book.imageUrl,
      "name" to book.name,
      "holder" to book.holder,
    )
  })
}

fun getBooks(): List<Book> {
  return localStorage.getItem(STORAGE_KEY)
    ?.let(::parseBookJson)
    ?: listOf(
      Book(
        id = "1",
        state = BookState.Available,
        year = "2019",
        imageUrl = "images/books/joy-kotlin.jpg",
        name = "Pierre Yves Saumont: The Joy of Kotlin",
        holder = null,
      ),
      Book(
        id = "2",
        state = BookState.Taken,
        year = "2019",
        imageUrl = "images/books/kotlin-android.jpg",
        name = "John Horton: Android Programming with Kotlin for Beginners: Build Android Apps Starting from Zero Programming Experience with the New Kotlin Programming Language",
        holder = null,
      ),
      Book(
        id = "3",
        state = BookState.Available,
        year = "2021",
        imageUrl = "images/books/kotlin-in-action.jpg",
        name = "Dmitry Jemerov: Kotlin in Action",
        holder = null,
      ),
      Book(
        id = "4",
        state = BookState.Soon,
        year = "2018",
        imageUrl = "images/books/kotlin-rogramming.jpg",
        name = "David Greenhalgh: Kotlin Programming: The Big Nerd Ranch Guide",
        holder = null,
      ),
      Book(
        id = "5",
        state = BookState.Available,
        year = "2018",
        imageUrl = "images/books/programming-in-kotlin.jpg",
        name = "Iyanu Adelekan: Kotlin Programming By Example: Build Real-world Android and Web Applications the Kotlin Way",
        holder = null,
      ),
      Book(
        id = "6",
        state = BookState.Available,
        year = "2019",
        imageUrl = "images/books/programming-kotlin.jpg",
        name = "Venkat Subramaniam: Programming Kotlin: Create Elegant, Expressive, and Performant JVM and Android Applications",
        holder = null,
      ),
    )
}

fun saveBooks(books: List<Book>) {
  localStorage.setItem(STORAGE_KEY, encodeBookJson(books))
}

fun initSearch() {
  val searchBar = document.getElementById("bookSearch") ?: return
  searchBar.addEventListener("input", {
    val query: String = it.target.asDynamic().value
    render(if (query.isNotBlank()) {
      getBooks().filter { book ->
        book.name.toLowerCase().contains(query.toLowerCase())
      }
    } else {
      getBooks()
    })
  })
}

fun main() {
  render(getBooks())
  initSearch()
}
