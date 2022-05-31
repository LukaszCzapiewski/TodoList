package com.example.todolist

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var todoAdapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        todoAdapter = TodoAdapter(mutableListOf())
        val database = FirebaseDatabase.getInstance()
        val db = FirebaseDatabase.getInstance().reference
        val taskRef = db.child("task")
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val value = ds.getValue(String::class.java)
                    var todo = value?.let { Todo(it) }
                    if (todo != null) {
                        todoAdapter.addTodo(todo)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", error.getMessage())
            }
        }
        taskRef.addListenerForSingleValueEvent(valueEventListener)
        rvTodoItems.adapter = todoAdapter
        rvTodoItems.layoutManager = LinearLayoutManager(this)



        btnAddTodo.setOnClickListener {
            val todoTitle = etTodoTitle.text.toString()
            if(todoTitle.isNotEmpty()) {
                val todo = Todo(todoTitle)
                todoAdapter.addTodo(todo)
                taskRef.child(todo.title).setValue(todo.title);
                etTodoTitle.text.clear()
            }
        }


        btnDeleteDoneTodos.setOnClickListener {
            todoAdapter.deleteDoneTodos()

        }
    }
}