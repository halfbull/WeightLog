package com.github.halfbull.weightlog.weightlog;

import android.support.annotation.NonNull;

class InputFilterTool {

    @NonNull
    CharSequence getFutureString(@NonNull CharSequence source, int start, int end, @NonNull CharSequence dest, int dStart, int dEnd) {
        CharSequence replacement = source.subSequence(start, end);
        return dest.subSequence(0, dStart).toString() + replacement + dest.subSequence(dEnd, dest.length()).toString();
    }

    boolean isDeleteAction(@NonNull CharSequence dest, @NonNull CharSequence futureString) {
        return dest.length() > futureString.length();
    }

    CharSequence getDeletedPart(@NonNull CharSequence dest, int dStart, int dEnd) {
        return dest.subSequence(dStart, dEnd);
    }
}
