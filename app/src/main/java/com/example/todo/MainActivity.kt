package com.example.todo

import android.app.Dialog
import android.app.Person
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
    private val taskReferences = mutableListOf<DatabaseReference>()

    private lateinit var rvCategories: RecyclerView
    private lateinit var categoriesAdapter: CategoriesAdapter

    private lateinit var rvTareas: RecyclerView
    private lateinit var tareasAdapter: TaskAdapter
    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var fabLogout: FloatingActionButton

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
        fabLogout = findViewById(R.id.fabLogout)
    }

    private fun initUI() {
        categoriesAdapter = CategoriesAdapter(categories) { categorySelected(it) }
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
        fabLogout.setOnClickListener {
            logout()
        }

    }

    private fun logout() {
        auth.signOut()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
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

                val newTask = Task(currentTask, currentCategory.toString())
                addTaskToFirebase(newTask)

                dialog.hide()
            }

        }
        dialog.show()
    }

    private fun addTaskToFirebase(newTask: Task) {
        val taskWithCategoryAsString = newTask.copy(category = newTask.category.toString())
        val newTaskRef = tasksRef.push()
        taskReferences.add(newTaskRef)
        newTaskRef.setValue(taskWithCategoryAsString).addOnCompleteListener { task ->
            if (!task.isSuccessful) Toast.makeText(this, "Error al agregar la tarea", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchTasksFromFirebase() {
        tasksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tasks.clear()
                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.getValue(Task::class.java)
                    if (task != null) {
                        tasks.add(task)
                    }
                }
                updateAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
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
        val newTasks = tasks.filter { task ->
            val taskCategory = TaskCategory.fromString(task.category) // Convertimos el String a TaskCategory
            selectedCategories.contains(taskCategory) // Comparamos objetos TaskCategory
        }
        tareasAdapter.tasks = newTasks
        tareasAdapter.notifyDataSetChanged()
    }
}