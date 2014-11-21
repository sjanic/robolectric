package org.robolectric.shadows;

import android.graphics.Paint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.TestRunners;
import org.robolectric.util.ShadowThingy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;


@RunWith(TestRunners.WithDefaults.class)
public class PaintTest {

  @Test
  public void shouldGetIsDitherInfo() {
    Paint paint = ShadowThingy.newInstanceOf(Paint.class);
    assertFalse(paint.isAntiAlias());
    ShadowPaint shadowPaint = shadowOf(paint);
    shadowPaint.setAntiAlias(true);
    assertTrue(paint.isAntiAlias());
  }

  @Test
  public void shouldGetIsAntiAlias() {
    Paint paint = ShadowThingy.newInstanceOf(Paint.class);
    assertFalse(paint.isAntiAlias());
    ShadowPaint shadowPaint = shadowOf(paint);
    shadowPaint.setAntiAlias(true);
    assertTrue(paint.isAntiAlias());
  }

  @Test
  public void testCtor() {
    Paint paint = ShadowThingy.newInstanceOf(Paint.class);
    assertFalse(paint.isAntiAlias());
    ShadowPaint shadowPaint = shadowOf(paint);
    shadowPaint.__constructor__( Paint.ANTI_ALIAS_FLAG );
    assertTrue(paint.isAntiAlias());
  }

  @Test
  public void shouldGetAndSetTextAlignment() throws Exception {
    Paint paint = ShadowThingy.newInstanceOf(Paint.class);
    assertThat(paint.getTextAlign()).isEqualTo(Paint.Align.LEFT);
    paint.setTextAlign(Paint.Align.CENTER);
    assertThat(paint.getTextAlign()).isEqualTo(Paint.Align.CENTER);
  }
}
