const userName = "БалтАртек2021";

const storageKey = "books";
const available = "available";
const taken = "taken";
const soon = "soon";

const bookStateClass = {
  available: "",
  taken: "book--on-hands",
  soon: "book--soon",
};

const buttonColorClass = {
  available: "",
  taken: "btn--maroon",
  soon: "btn--gray",
};

let books = JSON.parse(localStorage.getItem(storageKey)) ?? [
  new Book(available, 2019, "images/books/css.png", "Дэвид Макфарланд: Новая большая книга CSS"),
  new Book(taken, 2018, "images/books/js.png", "Изучаем программирование на JavaScript Фримен Эрик, Робсон Элизабет", "А. Степанков"),
  new Book(available, 2013, "images/books/html_css.png", "Джон Дакетт: HTML и CSS. Разработка и дизайн веб-сайтов"),
  new Book(soon, 2019, "images/books/js_inside.png", "К. Дуглас – Как устроен JavaScript"),
  new Book(available, 2017, "images/books/learn_js.png", "Э. Браун – Изучаем JavaScript. Руководство по созданию современных веб-сайтов"),
  new Book(available, 2010, "images/books/js_patterns.png", "С. Стефанов – JavaScript. Шаблоны"),
];

function render(search) {
  var content = document.getElementById("booksList");

  let html = "";
  getFiltered(search).map((book) => {
    html += toBookItem(book);
  });
  content.innerHTML = html;
}

function toBookItem(book) {
  return `<div class="book-list__item book ${bookStateClass[book.state]}" title="${book.name}">
    <img class="book__image" src="${book.imageUrl}" />
    <div class="book__info">
      <p class="book__year">${book.year}</p>
      <p class="book__name">${book.name}</p>
      <button class="book__btn btn btn--text ${buttonColorClass[book.state]}"
              onclick="bookClicked('${book.name}${book.year}')">
              ${getBookButtonCaption(book)}
      </button>
    </div>
  </div>`;
}

function doSearch(value) {
  render(value);
}

function bookClicked(id) {
  book = books.filter((book, i) => book.name + book.year === id)[0];
  if (book.state === available) {
    reserve(book);
  } else if (book.state === taken && book.holder === userName) {
    release(book);
  } else if (book.state === taken && book.holder != userName) {
    alert(`Книга находится у ${book.holder}.\nЕсли хорошо попросишь, может даст почитать...`);
  }
}

function reserve(book) {
  var index = books.indexOf(book);
  if (index > -1) {
    books[index].holder = userName;
    books[index].state = taken;
    localStorage.setItem(storageKey, JSON.stringify(books));
    render("");
  }
}

function release(book) {
  var index = books.indexOf(book);
  if (index > -1) {
    books[index].holder = null;
    books[index].state = available;
    localStorage.setItem(storageKey, JSON.stringify(books));
    render("");
  }
}

function getFiltered(search) {
  return books
    .filter((book, i) => !search || search.length <= 1 || book.name.toLowerCase().includes(search.toLowerCase()))
    .sort(function (b1, b2) {
      if (b1 > b2) {
        return 1;
      } else {
        return -1;
      }
    });
}

function getBookButtonCaption(book) {
  if (book.state === available) {
    return "Взять книгу";
  } else if (book.state === soon) {
    return "Скоро";
  } else if (book.state === taken && book.holder === userName) {
    return "Вернуть";
  } else if (book.state === taken && book.holder != userName) {
    return "Узнать у кого";
  }
}

document.addEventListener("DOMContentLoaded", () => render(""));
