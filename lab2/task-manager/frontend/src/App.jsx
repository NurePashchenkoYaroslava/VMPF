import React, { useState, useEffect } from 'react';
import { PieChart, Pie, Cell, Tooltip } from 'recharts';
import './App.css';

function App() {
  const [tasks, setTasks] = useState([]);
  const [user, setUser] = useState(null);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [selectedTask, setSelectedTask] = useState(null);
  
  const [analytics, setAnalytics] = useState({ total: 0, completed: 0, pending: 0, overdue: 0 });
  const [email, setEmail] = useState('');

  const fetchAnalytics = () => {
    fetch('http://localhost:3000/api/analytics')
      .then(res => res.json())
      .then(data => setAnalytics(data))
      .catch(err => console.error(err));
  };

  const fetchTasks = () => {
    fetch('http://localhost:3000/api/tasks')
      .then(res => res.json())
      .then(data => {
        setTasks(data);
        fetchAnalytics();
      })
      .catch(err => console.error(err));
  };

  useEffect(() => {
    fetchTasks();
  }, []);

  const handleLogin = (e) => {
    e.preventDefault();
    fetch('http://localhost:3000/api/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    })
    .then(res => {
      if (!res.ok) throw new Error('Невірний логін або пароль');
      return res.json();
    })
    .then(data => setUser(data))
    .catch(err => alert(err.message));
  };

  const handleAddTask = (e) => {
    e.preventDefault();
    fetch('http://localhost:3000/api/tasks', {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
        'x-user-role': user.role
      },
      body: JSON.stringify({ title, description, dueDate })
    })
    .then(res => res.json())
    .then(() => {
      setTitle(''); setDescription(''); setDueDate('');
      fetchTasks();
    });
  };

  const toggleComplete = (task) => {
    fetch(`http://localhost:3000/api/tasks/${task.id}`, {
      method: 'PUT',
      headers: { 
        'Content-Type': 'application/json',
        'x-user-role': user.role
      },
      body: JSON.stringify({ completed: !task.completed })
    })
    .then(() => fetchTasks());
  };

  const handleDeleteTask = (id) => {
    fetch(`http://localhost:3000/api/tasks/${id}`, {
      method: 'DELETE',
      headers: { 'x-user-role': user.role }
    })
    .then(res => {
      if (res.status === 403) alert('Тільки Адміністратор може видаляти задачі!');
      fetchTasks();
      if(selectedTask && selectedTask.id === id) setSelectedTask(null);
    });
  };

  const handleSendReminder = () => {
    if (!email) {
      alert("Будь ласка, введіть email!");
      return;
    }
    fetch('http://localhost:3000/api/send-reminders', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: user.username, email: email })
    })
    .then(res => res.json())
    .then(data => alert(data.message))
    .catch(err => alert('Помилка відправки'));
  };

  const isOverdue = (dateStr, completed) => {
    if (!dateStr || completed) return false;
    const today = new Date().toISOString().split('T')[0];
    return dateStr < today;
  };

  if (!user) {
    return (
      <div className="login-container">
        <h2>Task Manager</h2>
        <p style={{ color: 'var(--text-muted)', marginBottom: '20px' }}>Увійдіть, щоб продовжити</p>
        <form onSubmit={handleLogin}>
          <input type="text" placeholder="Логін (admin / user1)" value={username} onChange={e => setUsername(e.target.value)} required />
          <input type="password" placeholder="Пароль (123)" value={password} onChange={e => setPassword(e.target.value)} required />
          <button type="submit" style={{ width: '100%' }}>Увійти в систему</button>
        </form>
      </div>
    );
  }

  return (
    <div className="app-wrapper">
      <div className="header">
        <div>
          <h1 style={{ margin: 0 }}>Мої задачі</h1>
          <p style={{ margin: 10, color: 'var(--text-muted)' }}>Роль: <strong>{user.role}</strong></p>
        </div>
        <button className="btn-outline" onClick={() => setUser(null)}>Вийти ({user.username})</button>
      </div>

      <div className="main-content">
        {/* ЛІВА КОЛОНКА */}
        <div className="column">
          
          <div className="card">
            <h3 style={{ marginTop: 0 }}>Створити нову задачу</h3>
            <form onSubmit={handleAddTask}>
              <input type="text" placeholder="Назва задачі" value={title} onChange={e => setTitle(e.target.value)} required />
              <textarea placeholder="Детальний опис..." value={description} onChange={e => setDescription(e.target.value)} required></textarea>
              <input type="date" value={dueDate} onChange={e => setDueDate(e.target.value)} required />
              <button type="submit">Додати задачу</button>
            </form>
          </div>

          <div className="card">
            <h3 style={{ marginTop: 0 }}>Список задач</h3>
            <ul className="task-list">
              {tasks.map(task => {
                const overdue = isOverdue(task.dueDate, task.completed);
                return (
                  <li key={task.id} className={`task-item ${overdue ? 'overdue' : ''} ${task.completed ? 'task-completed' : ''}`}>
                    <input 
                      type="checkbox" 
                      checked={task.completed} 
                      onChange={() => toggleComplete(task)} 
                      style={{ width: 'auto', margin: 0, transform: 'scale(1.3)' }}
                    />
                    <div className="task-info" onClick={() => setSelectedTask(task)}>
                      <div className="task-title">{task.title}</div>
                      <div className="task-meta">Дедлайн: {task.dueDate || 'Немає'}</div>
                    </div>
                    {overdue && <span className="badge-overdue"> Протерміновано!</span>}
                    <button className="btn-danger" onClick={() => handleDeleteTask(task.id)} style={{ padding: '6px 12px', marginLeft: '15px' }}>
                      Видалити
                    </button>
                  </li>
                );
              })}
              {tasks.length === 0 && <p style={{color: 'var(--text-muted)'}}>Задач поки немає.</p>}
            </ul>
          </div>

        </div>

        {/* ПРАВА КОЛОНКА */}
        <div className="column">
          
          <div className="card">
            <h3 style={{ marginTop: 0 }}>Аналітика (Рівень 4)</h3>
            <div className="analytics-dashboard">
              <div>
                <div className="stat-item">Всього задач: <strong>{analytics.total}</strong></div>
                <div className="stat-item">Виконано: <strong style={{color: 'var(--success)'}}>{analytics.completed}</strong></div>
                <div className="stat-item">В процесі: <strong style={{color: 'var(--warning)'}}>{analytics.pending}</strong></div>
                <div className="stat-item">Протерміновано: <strong style={{color: 'var(--danger)'}}>{analytics.overdue}</strong></div>
              </div>

              {analytics.total > 0 && (
                <PieChart width={160} height={160}>
                  <Pie
                    data={[
                      { name: 'Виконано', value: analytics.completed, color: '#10b981' },
                      { name: 'В процесі', value: analytics.pending, color: '#f59e0b' },
                      { name: 'Протерміновано', value: analytics.overdue, color: '#ef4444' }
                    ].filter(item => item.value > 0)}
                    dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={70}
                  >
                    {[
                      { name: 'Виконано', value: analytics.completed, color: '#10b981' },
                      { name: 'В процесі', value: analytics.pending, color: '#f59e0b' },
                      { name: 'Протерміновано', value: analytics.overdue, color: '#ef4444' }
                    ].filter(item => item.value > 0).map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <Tooltip />
                </PieChart>
              )}
            </div>

            <div className="email-section">
              <input type="email" placeholder="Введіть email для звіту" value={email} onChange={e => setEmail(e.target.value)} />
              <button onClick={handleSendReminder} style={{ whiteSpace: 'nowrap' }}>Надіслати звіт</button>
            </div>
          </div>

          <div className="card">
            <h3 style={{ marginTop: 0 }}>Деталі задачі</h3>
            {selectedTask ? (
              <div>
                <h2 style={{ color: 'var(--primary)', marginTop: 0 }}>{selectedTask.title}</h2>
                <p style={{ lineHeight: '1.6' }}><strong>Опис:</strong><br/> {selectedTask.description}</p>
                <p><strong>Дедлайн:</strong> {selectedTask.dueDate}</p>
                <p>
                  <strong>Статус:</strong>{' '} 
                  <span style={{ color: selectedTask.completed ? 'var(--success)' : 'var(--warning)', fontWeight: 'bold' }}>
                    {selectedTask.completed ? 'Виконано' : 'В процесі'}
                  </span>
                </p>
              </div>
            ) : (
              <p style={{ color: 'var(--text-muted)' }}>Клікніть на задачу у списку ліворуч, щоб переглянути її деталі.</p>
            )}
          </div>

        </div>
      </div>
    </div>
  );
}

export default App;