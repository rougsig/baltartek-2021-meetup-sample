class Book {
  constructor(state, year, imageUrl, name, holder = null) {
    this.state = state;
    this.year = year;
    this.imageUrl = imageUrl;
    this.name = name;
    this.holder = holder;
  }
}
