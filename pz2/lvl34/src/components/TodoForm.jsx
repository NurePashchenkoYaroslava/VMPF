import React, { useState } from 'react';

const TodoForm = ({ addTodo }) => {
    const [inputValue, setInputValue] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        if (inputValue.trim()) {
            addTodo(inputValue);
            setInputValue('');
        }
    };

    return (
        <form onSubmit={handleSubmit} className="todo-form">
            <input 
                type="text" 
                value={inputValue}
                onChange={(e) => setInputValue(e.target.value)}
                placeholder="Що потрібно зробити?.."
                className="todo-input"
            />
            <button type="submit" className="todo-button">
                Додати
            </button>
        </form>
    );
};

export default TodoForm;