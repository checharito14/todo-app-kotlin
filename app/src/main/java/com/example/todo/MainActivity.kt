package com.example.todo

import android.app.Dialog
import android.app.Person
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val categories = listOf(
        TaskCategory.Business,
        TaskCategory.Personal,
        TaskCategory.Other
    )

    private val tasks = mutableListOf<Task>()

    private lateinit var rvCategories: RecyclerView
    private lateinit var categoriesAdapter: CategoriesAdapter

    private lateinit var rvTareas: RecyclerView
    private lateinit var tareasAdapter: TaskAdapter
    private lateinit var fabAddTask: FloatingActionButton

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var tasksRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()
        tasksRef = database.getReference("tasks").child(auth.currentUser?.uid ?: "")

        initComponent()
        initUI()
        initListeners()

        fetchTasksFromFirebase()
    }

    private fun initComponent() {
        rvCategories = findViewById(R.id.rvCategories)
        rvTareas = findViewById(R.id.rvTareas)
        fabAddTask = findViewById(R.id.fabAddTask)
    }

    private fun initUI() {
        categoriesAdapter = CategoriesAdapter(categories) { categorySelected(it)}
        rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvCategories.adapter = categoriesAdapter

        tareasAdapter = TaskAdapter(tasks) { itemSelected(it) }
        rvTareas.layoutManager = LinearLayoutManager(this)
        rvTareas.adapter = tareasAdapter
    }

    private fun initListeners() {
        fabAddTask.setOnClickListener {
            showDialog()
        }

    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_task)

        val btnAddTask: Button = dialog.findViewById(R.id.btnAddTask)
        val etTask: EditText = dialog.findViewById(R.id.etTask)
        val rgCategories: RadioGroup = dialog.findViewById(R.id.rgCategories)

        btnAddTask.setOnClickListener {
            val currentTask = etTask.text.toString()
            if (currentTask.isNotEmpty()) {
                val categoryId = rgCategories.checkedRadioButtonId
                val selectedRadioButton: RadioButton = rgCategories.findViewById(categoryId)
                val currentCategory: TaskCategory = when (selectedRadioButton.text) {
                    getString(R.string.business) -> TaskCategory.Business
                    getString(R.string.personal) -> TaskCategory.Personal
                    else -> TaskCategory.Other
                }

                val newTask = Task(currentTask, currentCategory)
                addTaskToFirebase(newTask)

                dialog.hide()
            }

        }

        dialog.show()
    }

    private fun addTaskToFirebase(newTask: Task) {
        val newTaskRef = tasksRef.push()
        newTaskRef.setValue(newTask).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                tasks.add(newTask)
                updateAdapter()
            } else {
                Toast.makeText(this, "Error al agregar la tarea", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchTasksFromFirebase() {
        tasksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tasks.clear()
                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.getValue(Task::class.java)
                    task?.let { tasks.add(it) }
                }
                updateAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error al cargar las tareas: ${error.message}",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun itemSelected(position: Int) {
        tasks[position].isSelected = !tasks[position].isSelected
        updateAdapter()
    }

    private fun categorySelected(position: Int) {
        categories[position].isSelected = !categories[position].isSelected
        categoriesAdapter.notifyItemChanged(position)
        updateAdapter()
    }

    private fun updateAdapter() {
        val selectedCategories: List<TaskCategory> = categories.filter { it.isSelected }
        val newTasks = tasks.filter { selectedCategories.contains(it.category) }
        tareasAdapter.tasks = newTasks
        tareasAdapter.notifyDataSetChanged()
    }
}