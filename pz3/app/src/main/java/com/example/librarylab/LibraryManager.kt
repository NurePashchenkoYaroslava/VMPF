package com.example.librarylab
data class Book(val id: Int, val title: String, val author: String, var totalCopies: Int, var availableCopies: Int = totalCopies)
data class Reader(val id: Int, val name: String, val borrowedBooks: MutableList<Book> = mutableListOf())


class LibraryManager {
    private val books = mutableListOf<Book>()
    private val readers = mutableListOf<Reader>()

    fun addBook(book: Book) {
        books.add(book)
    }

    fun addReader(reader: Reader) {
        readers.add(reader)
    }

    fun borrowBook(readerId: Int, bookId: Int): String {
        val reader = readers.find { it.id == readerId }
        val book = books.find { it.id == bookId }

        if (reader == null) return "Помилка: Читача не знайдено."
        if (book == null) return "Помилка: Книгу не знайдено."

        return if (book.availableCopies > 0) {
            book.availableCopies--
            reader.borrowedBooks.add(book)
            "Успіх: Книгу '${book.title}' видано читачу ${reader.name}."
        } else {
            "Відмова: Немає доступних примірників книги '${book.title}'."
        }
    }

    fun returnBook(readerId: Int, bookId: Int): String {
        val reader = readers.find { it.id == readerId }
        val book = books.find { it.id == bookId }

        if (reader == null) return "Помилка: Читача не знайдено."
        if (book == null) return "Помилка: Книгу не знайдено."

        val borrowedBook = reader.borrowedBooks.find { it.id == bookId }

        return if (borrowedBook != null) {
            reader.borrowedBooks.remove(borrowedBook)
            book.availableCopies++
            "Успіх: Книгу '${book.title}' повернуто."
        } else {
            "Помилка: Читач ${reader.name} не брав цю книгу."
        }
    }

    fun getLibraryStatus(): String {
        val status = StringBuilder("--- Стан Бібліотеки ---\n")
        books.forEach {
            status.append("Книга: '${it.title}' (Доступно: ${it.availableCopies}/${it.totalCopies})\n")
        }
        return status.toString()
    }
}