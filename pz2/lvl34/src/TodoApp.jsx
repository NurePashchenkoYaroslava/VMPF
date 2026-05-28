import React, { useState } from 'react';
import TodoForm from './components/TodoForm';
import './App.css'; 

const TodoApp = () => {
    const [todos, setTodos] = useState([
        { id: 1, text: 'Зробиту лабу 4', completed: true },
        { id: 2, text: 'Вигуляти равлика', completed: false },
        { id: 3, text: 'Приготувати суп', completed: false }
    ]);

    const [filter, setFilter] = useState('all');
    const [searchQuery, setSearchQuery] = useState('');
    const [sortOrder, setSortOrder] = useState('asc');

    const addTodo = (text) => {
        const newTodo = { id: Date.now(), text: text, completed: false };
        setTodos([...todos, newTodo]);
    };

    const toggleComplete = (id) => {
        setTodos(todos.map(todo => 
            todo.id === id ? { ...todo, completed: !todo.completed } : todo
        ));
    };
    let processedTodos = todos
        .filter(todo => todo.text.toLowerCase().includes(searchQuery.toLowerCase()))
        .filter(todo => {
            if (filter === 'completed') return todo.completed;
            if (filter === 'incomplete') return !todo.completed;
            return true;
        })
        .sort((a, b) => {
            if (sortOrder === 'asc') return a.text.localeCompare(b.text);
            return b.text.localeCompare(a.text);
        });

    const totalCount = todos.length;
    const completedCount = todos.filter(t => t.completed).length;
    const completionPercentage = totalCount === 0 ? 0 : Math.round((completedCount / totalCount) * 100);

    return (
        <div className="todo-container">
            <h2 className="todo-title">Мій список завдань</h2>
            <TodoForm addTodo={addTodo} />
            <div className="todo-panel">
                <input 
                    type="text" 
                    placeholder="Пошук завдань за текстом..." 
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="todo-search"
                />
                <select value={filter} onChange={(e) => setFilter(e.target.value)} className="todo-select">
                    <option value="all"> Всі завдання</option>
                    <option value="completed"> Тільки завершені</option>
                    <option value="incomplete"> Тільки незавершені</option>
                </select>
                <select value={sortOrder} onChange={(e) => setSortOrder(e.target.value)} className="todo-select">
                    <option value="asc"> Сортування: А-Я</option>
                    <option value="desc"> Сортування: Я-А</option>
                </select>
            </div>

            <ul className="todo-list">
                {processedTodos.map(todo => (
                    <li key={todo.id} className={`todo-item ${todo.completed ? 'completed' : 'active'}`}>
                        <label className={`todo-label ${todo.completed ? 'completed' : 'active'}`}>
                            <input 
                                type="checkbox" 
                                checked={todo.completed} 
                                onChange={() => toggleComplete(todo.id)} 
                                className="todo-checkbox"
                            />
                            {todo.text}
                        </label>
                    </li>
                ))}
                {processedTodos.length === 0 && (
                    <li className="todo-empty">Завдань не знайдено.</li>
                )}
            </ul>

            <hr className="todo-divider" />
            
            <h3 className="stat-title"> Статистика виконання</h3>       
            <table className="stat-table">
                <thead>
                    <tr>
                        <th className="stat-th">Всього</th>
                        <th className="stat-th">Виконано</th>
                        <th className="stat-th">Залишилось</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td className="stat-td">{totalCount}</td>
                        <td className="stat-td">{completedCount}</td>
                        <td className="stat-td">{totalCount - completedCount}</td>
                    </tr>
                </tbody>
            </table>

            <div className="progress-text">
                Прогрес успішності: {completionPercentage}%
                <div className="progress-track">
                    <div 
                        className={`progress-fill ${completionPercentage === 100 ? 'done' : 'in-progress'}`}
                        style={{ width: `${completionPercentage}%` }}
                    ></div>
                </div>
            </div>
        </div>
    );
};

export default TodoApp;