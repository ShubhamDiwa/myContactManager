package com.example.mycontactmanager.ui

import android.Manifest.*
import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.ContactsContract
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mycontactmanager.Iterfaces.ContactDelete
import com.example.mycontactmanager.R
import com.example.mycontactmanager.ViewModeFactory.ViewModelFactory
import com.example.mycontactmanager.ViewModel.ContactViewModel
import com.example.mycontactmanager.adapter.ContactAdapter
import com.example.mycontactmanager.database.ContactDB
import com.example.mycontactmanager.databinding.ActivityMainBinding
import com.example.mycontactmanager.model.ContactModel
import com.example.mycontactmanager.repo.ContactRepo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.stream.Collectors

private const val TAG = "MAINACTIVITY"

class MainActivity : AppCompatActivity(), ContactDelete {

    lateinit var viewModel: ContactViewModel
    val permission = arrayOf(android.Manifest.permission.READ_CONTACTS)
    val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var contacList: ArrayList<ContactModel> = arrayListOf()
    private var backUpContacList: ArrayList<ContactModel> = arrayListOf()
    val permissionCode = 14
    private lateinit var adapter: ContactAdapter
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.tilSearchBar.visibility = View.GONE
        searchBar()
        refresh()

        binding.ivSearch.setOnClickListener {
            if (binding.tilSearchBar.visibility == View.VISIBLE) {
                binding.tilSearchBar.visibility = View.GONE
            } else {
                binding.tilSearchBar.visibility = View.VISIBLE
            }
        }

        viewModel = ViewModelProvider(
            this, ViewModelFactory(ContactRepo(ContactDB.getDatabase(this).getContactDao()))
        )[ContactViewModel::class.java]
        adapter = ContactAdapter(this, contacList)
        binding.rvContact.adapter = adapter
        observer()
    }

    private fun refresh() {
        binding.idRefresh.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.deleteAll()
            }
            binding.idRefresh.animate().setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    binding.idRefresh.setColorFilter(
                        ContextCompat.getColor(
                            baseContext,
                            R.color.purple
                        )
                    )
                }

                override fun onAnimationEnd(animation: Animator) {
                    binding.idRefresh.setColorFilter(
                        ContextCompat.getColor(
                            baseContext,
                            R.color.white
                        )
                    )
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).rotationBy(360f).setDuration(1000).start()
            getAllContacts()
            binding.progress.visibility = View.VISIBLE
        }


    }

    private fun searchBar() {
        binding.idSearchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(query: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (TextUtils.isEmpty(query.toString())) {
                    contacList.clear()
                    contacList.addAll(backUpContacList)
                    adapter.notifyDataSetChanged()
                    Log.e(TAG, "onTextChanged: empty data")
                } else {
                    Log.e(TAG, "onTextChanged: not empty data ${query.toString()}")
                    //query?.let { itQuery ->
                    contacList.clear()
                    contacList.addAll(
                        backUpContacList.stream().filter {
                            return@filter it.name?.lowercase()?.trim()?.contains(query.toString().lowercase())
                                ?: false
                        }.collect(Collectors.toList())
                    )
                    if (contacList.isEmpty()) {
//                        Toast.makeText(this@MainActivity, "No record available", Toast.LENGTH_SHORT).show()
                    } else {
                        adapter.notifyDataSetChanged()
                    }

                    //}
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        /*binding.idSearchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.e(
                    TAG,
                    "onQueryTextSubmit: query : $query ${contacList.size} ${backUpContacList.size}"
                )
                query?.let { itQuery ->
                    contacList.clear()
                    contacList.addAll(
                        backUpContacList.stream().filter {
                            return@filter it.name.lowercase().trim().contains(itQuery.lowercase())
                        }.collect(Collectors.toList())
                    )
                    adapter.notifyDataSetChanged()
                }
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                Log.e(
                    TAG,
                    "onQueryTextChange: query : $query ${contacList.size} ${backUpContacList.size}"
                )
                //adapter.filterList(p0)
                query?.let { itQuery ->
                    contacList.clear()
                    contacList.addAll(
                        backUpContacList.stream().filter {
                            return@filter it.name.lowercase().trim().contains(itQuery.lowercase())
                        }.collect(Collectors.toList())
                    )
                    adapter.notifyDataSetChanged()
                }
                return false
            }
        })*/
    }

    private fun progressDialog() {
        val dialog = Dialog(this@MainActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.activity_main)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        object : CountDownTimer(8000, 1000) {
            override fun onTick(l: Long) {
                // Do nothing
            }

            override fun onFinish() {
                dialog.dismiss()
            }
        }.start()

    }

//    private fun askpermission() {
//        ActivityCompat.requestPermissions(this, permission, permissionCode)
//
//    }
//
//    private fun allPermissionGranted(): Boolean {
//        for (item in permission) {
//            if (ContextCompat.checkSelfPermission(
//                    this,
//                    item
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                return false
//            }
//        }
//        return true
////        binding.progress.visibility= View.GONE
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode == permissionCode) {
//
//            if (allPermissionGranted()) {
//                Log.e("Hello", "onRequestPermissionsResult: All permission Granted")
//                CoroutineScope(Dispatchers.Main).launch {
////                    fetchContacts()
//                    getAllContacts()
//                    binding.progress.visibility = View.VISIBLE
//                }
//            } else {
//                askpermission()
//                binding.progress.visibility = View.VISIBLE
//            }
//        }
//    }
//
private val permissionRequestForReadContacts =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // PERMISSION GRANTED
            Log.e(TAG, "onRequestPermissionsResult: All permission Granted")
            CoroutineScope(Dispatchers.Main).launch {
                fetchContacts()
            }

        } else {
            // PERMISSION NOT GRANTED
            if (Build.VERSION.SDK_INT < 33) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        android.Manifest.permission.READ_CONTACTS
                    )
                ) {
                    //todo check permission
                    checkForContactsPermission { isGranted ->
                        if (isGranted) {
                            CoroutineScope(Dispatchers.Main).launch {
                                fetchContacts()
                            }
                        }
                    }

                } else {
                    //todo open setting
                    openPhoneSettings("Contacts")
                }
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        android.Manifest.permission.READ_CONTACTS                    )
                ) {
                    //todo check permission
                    checkForContactsPermission { isGranted ->
                        if (isGranted) {
                            CoroutineScope(Dispatchers.Main).launch {
                                fetchContacts()
                            }
                        }
                    }
                } else {
                    //todo check permission
                    openPhoneSettings("Contacts")

                }
            }
        }
    }

    private fun checkForContactsPermission(isPermissionGranted: (isGranted: Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT < 33) {
            if (ContextCompat.checkSelfPermission(
                    this,
                   android. Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                isPermissionGranted(true)
            }
            else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                   android. Manifest.permission.READ_CONTACTS
                )
            ) {
                MaterialAlertDialogBuilder(
                    this,
                ).setTitle("Read Contacts")
                    .setMessage("To get Contacts we require this permission.")
                    .setCancelable(false)
                    .setPositiveButton("Continue") { d: DialogInterface?, w: Int ->
                        permissionRequestForReadContacts.launch(android.Manifest.permission.READ_CONTACTS)
                    }.setNegativeButton("Cancel") { d: DialogInterface, w: Int ->
                        d.dismiss()
                        openPhoneSettings("Contacts")
                    }
                    .show()
            }
            else {
                permissionRequestForReadContacts.launch(android.Manifest.permission.READ_CONTACTS)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                isPermissionGranted(true)
            }
            else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.READ_CONTACTS
                )
            ) {
                MaterialAlertDialogBuilder(
                    this,
                ).setTitle("Read Contacts")
                    .setMessage("To get Contacts we require this permission.")
                    .setCancelable(false)
                    .setPositiveButton("Continue") { d: DialogInterface?, w: Int ->
                        permissionRequestForReadContacts.launch(android.Manifest.permission.READ_CONTACTS)
                    }.setNegativeButton("Cancel") { d: DialogInterface, w: Int ->
                        d.dismiss()
                        openPhoneSettings("Contacts")
                    }
                    .show()
            }
            else {
                permissionRequestForReadContacts.launch(android.Manifest.permission.READ_CONTACTS)
            }
        }
    }
    private fun openPhoneSettings(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Permission required")
            .setMessage("Goto app's setting and grant following permission -> $message")
            .setPositiveButton("Open Settings"
            ) { p0, p1 ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val uri = Uri.fromParts(
                    "package", packageName, null
                )
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel"
            ) { p0, p1 ->

                p0?.dismiss()
                openPhoneSettings("Contacts")
            }.show()

    }
    private fun observer() {
        viewModel.getCont.observe(this) {
            checkForContactsPermission { itGranted->
                if (itGranted){
                    if (it.isEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            fetchContacts()
                        }
                    } else {
                        clearAndUpdate(it)
                        //contacList.addAll(it)
                        binding.progress.visibility = View.GONE
                    }
                }

            }
        }
    }


    private fun clearAndUpdate(it: List<ContactModel>?) {
        contacList.clear()
        backUpContacList.clear()
        if (it != null) {
            contacList.addAll(it)
            backUpContacList.addAll(it)
        }
        adapter.notifyDataSetChanged()
    }
    @SuppressLint("Range")
    private fun fetchContacts() {
        val cr = contentResolver
        val cursor: Cursor? =
            cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

        if (cursor != null && cursor.count > 0) {

            while (cursor != null && cursor.moveToNext()) {
                val id =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val hasPhoneNum =
                    cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                if (hasPhoneNum > 0) {
                    val pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        ""
                    )

                    if (pCur != null && pCur.count > 0) {

                        while (pCur != null && pCur.moveToNext()) {
                            val phoneNum =
                                pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                            if (name.isNotEmpty() || phoneNum.isNotEmpty()) {
                                Log.e("MYDATA", "fetchContacts: $name and $phoneNum")
                                //currentContact = ContactsDto(null, name, phoneNum)
                                viewModel.insertC(ContactModel(name, phoneNum, null, null))

                            }

                        }
                        pCur.close()

                    }

                }

            }

            cursor.close()


        }
    }

    @SuppressLint("Range")
    private fun getAllContacts() {

        val contacts = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null
        )

        if (contacts != null) {
            while (contacts.moveToNext()) {
                var name =
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY))
                var number =
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                var contactId =
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                Log.e(
                    "Cont",
                    "getAllContacts:name =${name} number =${number} contactID=${contactId} "
                )
                viewModel.insertC(ContactModel(name, number, contactId, null))
            }
            contacts.close()
        }
    }

    override fun delete(delete: ContactModel) {

        val builder = AlertDialog.Builder(this)
        with(builder) {
            setTitle("Do you Want to Delete")
            setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    viewModel.deleteC(delete)
                    binding.progress.visibility = View.VISIBLE
                    adapter.notifyDataSetChanged()
                }
            })
            setNegativeButton("No", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    p0?.dismiss()
                }
            })
            show()
        }
    }

    override fun share(share: ContactModel) {
        val intent = Intent()
        val Details = share.name + "\n" + share.phone
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, "$Details")
        intent.type = "text/plain"
        startActivity(Intent.createChooser(intent, "Share To:"))
    }

    override fun call(call: ContactModel) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        val call = call.phone
        dialIntent.data = Uri.parse("tel:$call")
        startActivity(dialIntent)
    }
}