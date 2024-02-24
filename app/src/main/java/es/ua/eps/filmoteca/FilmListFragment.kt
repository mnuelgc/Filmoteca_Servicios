package es.ua.eps.filmoteca

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.ActionMode
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.CheckBox
import android.widget.ListView
import androidx.fragment.app.ListFragment
import es.ua.eps.filmoteca.databinding.FragmentFilmDataBinding
import es.ua.eps.filmoteca.databinding.FragmentFilmListBinding
import kotlin.ClassCastException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FilmListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FilmListFragment : ListFragment() {
    var callback : OnItemSelectedListener? = null
    public lateinit var adapter : FilmsAdapter


    interface OnItemSelectedListener {
        fun onItemSelected(position: Int)
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        res = resources
        cont = requireContext()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentFilmListBinding.inflate(layoutInflater)
        val list = binding.list
        registerForContextMenu(list)

        adapter = FilmsAdapter(
            cont,
            R.layout.item_film, FilmDataSource.films
        )

        list.adapter = adapter

        list.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
        //list.setSelector(android.R.color.darker_gray);
        list.setMultiChoiceModeListener(
            object : AbsListView.MultiChoiceModeListener {
                override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                    val inflater = mode.menuInflater
                    inflater.inflate(R.menu.menu_contextual_multiple,menu)
                    val numElement = list.adapter.count
                    for (position :Int in 0 ..< numElement) {
                        list.getChildAt(position)?.findViewById<CheckBox>(R.id.item_check)?.visibility = View.VISIBLE
                    }
                    return true
                }

                override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                    return false
                }

                override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                    return when(item.itemId) {
                        R.id.deleteFilms ->{

                            val listOfIndexToRemove = ArrayList<Int>()

                            for (position: Int in 0..<list.adapter?.count!! )
                            {
                                var isChecked = false
                                isChecked = list.getChildAt(position).findViewById<CheckBox>(R.id.item_check).isChecked

                                if (isChecked)
                                    listOfIndexToRemove.add(position)
                            }

                            listOfIndexToRemove.sortDescending()

                            for (position : Int in listOfIndexToRemove)
                            {
                                val lastPositon = list.adapter?.count!! -1
                                val checkBox = list.getChildAt(position).findViewById<CheckBox>(R.id.item_check)
                                val lastCheckBox = list.getChildAt(lastPositon).findViewById<CheckBox>(R.id.item_check)

                                if(checkBox.isChecked){
                                    checkBox.isChecked = false
                                    lastCheckBox.visibility= View.INVISIBLE
                                    FilmDataSource.films.removeAt(position)
                                }
                            }
                            (list.adapter as FilmsAdapter)?.notifyDataSetChanged()
                            if (list.adapter.count<= 0){
                                AddNewFilmToList()
                            }

                            listOfIndexToRemove.clear()
                            return true
                        }
                        else -> false
                    }
                }

                override fun onDestroyActionMode(mode: ActionMode?) {
                    val itemsRemain = list.adapter?.count

                    for (position : Int in 0..<itemsRemain!!)
                    {
                        val checkBox = list.getChildAt(position).findViewById<CheckBox>(R.id.item_check)
                        checkBox.isChecked = false
                        checkBox.visibility = View.INVISIBLE
                    }
                }

                override fun onItemCheckedStateChanged(
                    mode: ActionMode?,
                    position: Int,
                    id: Long,
                    checked: Boolean
                ) {

                    if (checked){
                        val checkBox = list.getChildAt(position).findViewById<CheckBox>(R.id.item_check)

                        checkBox.isChecked = true
                        checkBox.visibility = View.VISIBLE
                    }
                }
            }
        )


        return binding.root
    }

    override fun onListItemClick(l: ListView, v: View,
                                 position: Int, id: Long) {
        callback?.onItemSelected(position)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        callback = try {
            context as OnItemSelectedListener
        } catch (e: ClassCastException) {
            throw  ClassCastException(context.toString() + "debe implementar OnItemSelectedListener")
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FilmListFragment.
         */
        lateinit var res : Resources
        lateinit var cont : Context


        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FilmListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.menu_principal,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when(item.itemId){
            R.id.addFilm ->{
                AddNewFilmToList()
                return true
            }
            R.id.about ->{
                val intent = Intent(activity, AboutActivity::class.java)
                startActivity(intent)
                return true
            }
        }

        return false
    }

    private fun  AddNewFilmToList()
    {
        val film = Film(context)
        FilmDataSource.films.add(film)
        adapter.notifyDataSetChanged()
    }
}