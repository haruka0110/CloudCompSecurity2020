import logo from './logo.svg';
import React from 'react';
import './App.css';
import firebase from './firebase.js';

function TodoForm({addTodo}) {
  const [value, setValue] = React.useState("");

  const handleSubmit = e => {
    e.preventDefault();
    if (!value) return;
    addTodo(value);
    setValue("");
  };

  return (
    <form onSubmit={handleSubmit}>
      <label>+ </label>
      <input
        type="text"
        className="input"
        value={value}
        onChange={e => setValue(e.target.value)}
        placeholder="new todo"
      />
    </form>
  );
}
//

class App extends React.Component {

  constructor(props) {
    super(props);
    this.state = {isLoaded: false, todos: []};
  }

  componentDidMount() {
    const db = firebase.database();
    const refs = db.ref('todolist');
    refs.on('value', (snapshot) =>{
      let DBtodos = snapshot.val();
      let parsed = [];
      const keys = Object.keys(DBtodos);
      keys.forEach(function(key){
          const todo = {
            id: key,
            text: DBtodos[key]['text'],
            isCompleted: DBtodos[key]['isCompleted']
          }
          parsed.push(todo)
      })
      this.setState({
        todos: parsed 
      }, () => {
        this.setState({ isLoaded: true })
      })
    })
  }

  Todo = (todo) => {
    console.log(todo.id);
    return (
      <div 
        className="todo"
        style={{ textDecoration: todo.isCompleted ? "line-through" : "" }}
        key={todo.id}
      >
        {todo.text}
        <div>
          <button onClick={() => {this.completeTodo(todo.id)}}>Complete</button>
          <button onClick={() => {this.removeTodo(todo.id)}}>x</button>
        </div>
      </div>
    );
  };

  addTodo = (text) =>{
    const newPostKey = firebase.database().ref().child('todolist').push().key;
    firebase.database().ref('todolist/'+ newPostKey ).set({
      text: text,
      isCompleted: false
    });
    const todo = {
      id: newPostKey,
      text: text,
      isCompleted: false
    }
    let newTodos = this.state.todos;
    newTodos.push(todo);
    this.setState({ todos: newTodos });
  }

  completeTodo = (id) => {
    //console.log(this.state.todos);
    let newTodos = this.state.todos;
    newTodos.find(x => x.id === id).isCompleted = true;
    this.setState({ todos: newTodos });
    
    const db = firebase.database();
    const refs = db.ref('todolist/'+id);
    refs.update({"isCompleted": true});
  };

  removeTodo = (id) => {
    let newTodos = this.state.todos;
    delete newTodos[id]; 
    this.setState({ todos: newTodos });
    console.log(this.state.todos);
    const db = firebase.database();
    const refs = db.ref('todolist/'+id);
    refs.remove();
    //db.collection('todolist').doc(id).delete();
  };

  render() {
    return ( 
      <div className="app">
        <h1>Todo List</h1>
        <div className="todo-list">
        {this.state.isLoaded ? this.state.todos.map((todo) => {return this.Todo(todo)})
          : 'Loading...'}
        <TodoForm addTodo={this.addTodo} /></div></div>
    );
  }
}

export default App;
