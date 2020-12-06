package com.harsh.hkutils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class Tuple<A,B,C>{
	public final A a;
	public final B b;
	public final C c;
	public Tuple(A a, B b, C c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Tuple<?, ?, ?> tuple = (Tuple<?, ?, ?>) o;
		return Objects.equals(a, tuple.a) &&
				Objects.equals(b, tuple.b) &&
				Objects.equals(c, tuple.c);
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	@Override
	public int hashCode() {
		return Objects.hash(a, b, c);
	}
}
