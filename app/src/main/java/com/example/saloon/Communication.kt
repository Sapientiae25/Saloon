package com.example.saloon

import android.graphics.Bitmap
import androidx.fragment.app.Fragment

interface DeleteEvent { fun deletes() }
interface CloseSheet { fun close() }
interface ChangeFragment { fun change(fragment: Fragment) }
