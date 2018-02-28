/*
    This file is part of Privacy Friendly Pain Diary.

    Privacy Friendly Pain Diary is free software: you can redistribute it
    and/or modify it under the terms of the GNU General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.secuso.privacyfriendlypaindiary.database.entities.interfaces;

import org.secuso.privacyfriendlypaindiary.database.entities.enums.BodyRegion;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.PainQuality;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Time;

import java.util.EnumSet;

/**
 * A pain description encapsulates information about the pain a user is
 * experiencing, i.e. the intensity, nature (qualities) and times of the pain,
 * as well as the affected body regions.
 * </p>
 * Since users are not required to enter any information, some of the methods
 * might return empty sets or <code>null</code>.
 *
 * @author Susanne Felsen
 * @version 20180110
 */
public interface PainDescriptionInterface extends PersistentObject {

    int getPainLevel();

    void setPainLevel(int painLevel);

    EnumSet<BodyRegion> getBodyRegions();

    void setBodyRegions(EnumSet<BodyRegion> bodyRegions);

    EnumSet<PainQuality> getPainQualities();

    void setPainQualities(EnumSet<PainQuality> painQualities);

    EnumSet<Time> getTimesOfPain();

    void setTimesOfPain(EnumSet<Time> timesOfPain);

}
