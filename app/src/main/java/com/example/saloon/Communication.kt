package com.example.saloon

import androidx.fragment.app.Fragment

interface DeleteEvent { fun deletes() }
interface RestartCalendar { fun restart() }
interface CloseSheet { fun close() }
interface ChangeFragment { fun change(fragment: Fragment) }
interface LastFragment { fun back() }
